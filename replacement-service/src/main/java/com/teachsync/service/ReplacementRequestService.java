package com.teachsync.service;

import com.teachsync.domain.ReplacementRequest;
import com.teachsync.domain.ReplacementResponse;
import com.teachsync.domain.ResponseStatus;
import com.teachsync.domain.Status;
import com.teachsync.dto_s.replacementRequest.ReplacementRequestBaseDto;
import com.teachsync.dto_s.replacementRequest.ReplacementRequestCreateDto;
import com.teachsync.interaction.feign.clients.groupCourse.GroupCourseClient;
import com.teachsync.interaction.feign.clients.schedule.ScheduleClient;
import com.teachsync.interaction.feign.clients.users.UserClient;
import com.teachsync.interaction.kafka.ReplacementEventProducer;
import com.teachsync.interaction.requests.ScheduleBaseDtoRequest;
import com.teachsync.interaction.requests.WeekDays;
import com.teachsync.interaction.requests.nested.GroupCourseBaseInfoRequest;
import com.teachsync.interaction.requests.nested.SpecializationsBaseDto;
import com.teachsync.interaction.requests.nested.TeacherBaseInfoRequest;
import com.teachsync.mappers.ReplacementMapper;
import com.teachsync.repository.ReplacementRequestRepository;
import com.teachsync.repository.ReplacementResponseRepository;
import com.teachsync.teachsyncevents.replacements.ReplacementApprovedEvent;
import com.teachsync.teachsyncevents.replacements.ReplacementRequestedEvent;
import com.teachsync.teachsyncevents.replacements.ReplacementStatusChangedEvent;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ReplacementRequestService {

    private static final Set<Status> ACTIVE_STATUSES = Set.of(Status.PENDING, Status.APPROVED);
    private static final Set<Status> PROBLEMATIC_STATUSES = Set.of(Status.PENDING, Status.EXPIRED, Status.AUTO_CLOSED);

    private final ReplacementRequestRepository repository;
    private final ReplacementResponseRepository responseRepository;
    private final ScheduleClient scheduleClient;
    private final GroupCourseClient groupCourseClient;
    private final UserClient userClient;
    private final ReplacementEventProducer eventProducer;

    public ReplacementRequestService(ReplacementRequestRepository repository,
                                     ReplacementResponseRepository responseRepository,
                                     ScheduleClient scheduleClient,
                                     GroupCourseClient groupCourseClient,
                                     UserClient userClient,
                                     ReplacementEventProducer eventProducer) {
        this.repository = repository;
        this.responseRepository = responseRepository;
        this.scheduleClient = scheduleClient;
        this.groupCourseClient = groupCourseClient;
        this.userClient = userClient;
        this.eventProducer = eventProducer;
    }

    @Transactional
    public ReplacementRequestBaseDto create(ReplacementRequestCreateDto dto) {
        ScheduleBaseDtoRequest schedule = scheduleClient.getSchedule(dto.getScheduleId());
        GroupCourseBaseInfoRequest groupCourse = groupCourseClient.groupCourseBaseInfoRequest(schedule.getGroupCourseDto().getId());
        Long teacherRequested = dto.getTeacherRequested() != null
                ? dto.getTeacherRequested()
                : schedule.getTeacherDto().getId();

        validateLessonDate(dto.getLessonDate(), schedule.getEndTime());
        if (repository.existsByScheduleIdAndLessonDateAndStatusIn(schedule.getId(), dto.getLessonDate(), ACTIVE_STATUSES)) {
            throw new IllegalStateException("Active replacement already exists for this lesson");
        }

        ReplacementRequest request = new ReplacementRequest(
                schedule.getId(),
                groupCourse.getId(),
                LocalDateTime.now(),
                dto.getLessonDate(),
                null,
                dto.getReason(),
                teacherRequested,
                Status.PENDING
        );
        ReplacementRequest saved = repository.save(request);

        List<TeacherBaseInfoRequest> candidates = findCandidateTeachers(saved, schedule, groupCourse);
        if (candidates.isEmpty()) {
            saved.setStatus(Status.AUTO_CLOSED);
            publishStatusChanged(saved, groupCourse, schedule,
                    "Система не нашла свободных преподавателей с подходящей специализацией.");
            return enrich(saved);
        }

        for (TeacherBaseInfoRequest candidate : candidates) {
            responseRepository.save(new ReplacementResponse(saved, ResponseStatus.PENDING, candidate.getId()));
            eventProducer.publishReplacementRequested(new ReplacementRequestedEvent(
                    saved.getId(),
                    teacherRequested,
                    candidate.getId(),
                    schedule.getId(),
                    groupCourse.getCourseName(),
                    groupCourse.getGroupName(),
                    schedule.getClassRoomBaseDto() == null ? null : schedule.getClassRoomBaseDto().getName(),
                    saved.getLessonDate(),
                    schedule.getStartTime(),
                    schedule.getEndTime(),
                    saved.getReason()
            ));
        }

        return enrich(saved);
    }

    public ReplacementRequestBaseDto getRequestById(Long id) {
        ReplacementRequest replacementRequest = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Replacement request not found"));
        return enrich(replacementRequest);
    }

    public List<ReplacementRequestBaseDto> getForTeacher(Long teacherId) {
        return repository.findVisibleForTeacher(teacherId)
                .stream()
                .map(this::enrich)
                .toList();
    }

    public List<ReplacementRequestBaseDto> getProblematicRequests() {
        return repository.findByStatusInOrderByPriority(PROBLEMATIC_STATUSES)
                .stream()
                .map(this::enrich)
                .toList();
    }

    @Transactional
    public ReplacementRequestBaseDto approve(Long requestId, Long teacherId) {
        ReplacementRequest request = getPendingRequest(requestId);
        ReplacementResponse response = responseRepository
                .findByReplacementRequestIdAndTeacherResponse(requestId, teacherId)
                .orElseThrow(() -> new IllegalArgumentException("Teacher was not invited to this replacement"));

        ScheduleBaseDtoRequest schedule = scheduleClient.getSchedule(request.getScheduleId());
        GroupCourseBaseInfoRequest groupCourse = groupCourseClient.groupCourseBaseInfoRequest(request.getGroupCourseId());
        TeacherBaseInfoRequest teacher = userClient.getTeacher(teacherId);

        if (!isTeacherAvailableForSchedule(schedule, request, teacherId) || !hasRequiredSpecialization(teacher, groupCourse.getCategoryId())) {
            throw new IllegalStateException("Teacher is no longer available or does not match course category");
        }

        response.setResponseStatus(ResponseStatus.ACCEPTED);
        request.setApprovedById(teacherId);
        request.setStatus(Status.APPROVED);
        responseRepository.findByReplacementRequestId(requestId).stream()
                .filter(other -> !teacherId.equals(other.getTeacherResponse()))
                .forEach(other -> other.setResponseStatus(ResponseStatus.DECLINED));

        eventProducer.publishReplacementApproved(new ReplacementApprovedEvent(
                request.getId(),
                request.getTeacherRequested(),
                teacherId,
                teacher.displayName(),
                teacher.getEmail(),
                groupCourse.getCourseName(),
                groupCourse.getGroupName(),
                schedule.getClassRoomBaseDto() == null ? null : schedule.getClassRoomBaseDto().getName(),
                request.getLessonDate(),
                schedule.getStartTime(),
                schedule.getEndTime()
        ));

        return enrich(request);
    }

    @Transactional
    public ReplacementRequestBaseDto decline(Long requestId, Long teacherId) {
        ReplacementRequest request = getPendingRequest(requestId);
        ReplacementResponse response = responseRepository
                .findByReplacementRequestIdAndTeacherResponse(requestId, teacherId)
                .orElseThrow(() -> new IllegalArgumentException("Teacher was not invited to this replacement"));

        if (response.getResponseStatus() == ResponseStatus.ACCEPTED) {
            throw new IllegalStateException("Accepted replacement cannot be declined");
        }

        response.setResponseStatus(ResponseStatus.DECLINED);
        autoCloseIfNoPendingCandidates(request);
        return enrich(request);
    }

    @Transactional
    public ReplacementRequestBaseDto cancel(Long requestId, Long teacherId) {
        ReplacementRequest request = getPendingRequest(requestId);
        if (!teacherId.equals(request.getTeacherRequested())) {
            throw new IllegalArgumentException("Only requesting teacher can cancel replacement");
        }
        request.setStatus(Status.CANCELLED);
        responseRepository.findByReplacementRequestId(requestId)
                .forEach(response -> response.setResponseStatus(ResponseStatus.DECLINED));

        ScheduleBaseDtoRequest schedule = scheduleClient.getSchedule(request.getScheduleId());
        GroupCourseBaseInfoRequest groupCourse = groupCourseClient.groupCourseBaseInfoRequest(request.getGroupCourseId());
        publishStatusChanged(request, groupCourse, schedule, "Заявка на замену была отменена преподавателем.");
        return enrich(request);
    }

    @Scheduled(fixedDelay = 300000)
    @Transactional
    public void expireStaleRequests() {
        for (ReplacementRequest request : repository.findByStatus(Status.PENDING)) {
            ScheduleBaseDtoRequest schedule = scheduleClient.getSchedule(request.getScheduleId());
            if (!lessonAlreadyEnded(request.getLessonDate(), schedule.getEndTime())) {
                continue;
            }
            request.setStatus(Status.EXPIRED);
            responseRepository.findByReplacementRequestId(request.getId())
                    .forEach(response -> response.setResponseStatus(ResponseStatus.DECLINED));
            GroupCourseBaseInfoRequest groupCourse = groupCourseClient.groupCourseBaseInfoRequest(request.getGroupCourseId());
            publishStatusChanged(request, groupCourse, schedule, "Время занятия прошло, заявка автоматически помечена как просроченная.");
        }
    }

    private List<TeacherBaseInfoRequest> findCandidateTeachers(ReplacementRequest request,
                                                               ScheduleBaseDtoRequest schedule,
                                                               GroupCourseBaseInfoRequest groupCourse) {
        Set<Long> freeTeacherIds = new HashSet<>(
                scheduleClient.getAvailableTeachers(schedule.getId(), weekdayFromLessonDate(request.getLessonDate()))
        );
        return userClient.getAllByRole("TEACHER")
                .stream()
                .filter(teacher -> freeTeacherIds.contains(teacher.getId()))
                .filter(teacher -> !teacher.getId().equals(request.getTeacherRequested()))
                .filter(teacher -> hasRequiredSpecialization(teacher, groupCourse.getCategoryId()))
                .toList();
    }

    private ReplacementRequest getPendingRequest(Long requestId) {
        ReplacementRequest request = repository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Replacement request not found"));
        if (request.getStatus() != Status.PENDING) {
            throw new IllegalStateException("Replacement request is already closed");
        }
        return request;
    }

    private void autoCloseIfNoPendingCandidates(ReplacementRequest request) {
        boolean hasPending = responseRepository.findByReplacementRequestId(request.getId()).stream()
                .anyMatch(response -> response.getResponseStatus() == ResponseStatus.PENDING);
        if (hasPending) {
            return;
        }
        request.setStatus(Status.AUTO_CLOSED);
        ScheduleBaseDtoRequest schedule = scheduleClient.getSchedule(request.getScheduleId());
        GroupCourseBaseInfoRequest groupCourse = groupCourseClient.groupCourseBaseInfoRequest(request.getGroupCourseId());
        publishStatusChanged(request, groupCourse, schedule, "Все приглашенные преподаватели отказались или не подтвердили замену.");
    }

    private boolean isTeacherAvailableForSchedule(ScheduleBaseDtoRequest schedule,
                                                  ReplacementRequest request,
                                                  Long teacherId) {
        return scheduleClient.getAvailableTeachers(schedule.getId(), weekdayFromLessonDate(request.getLessonDate()))
                .contains(teacherId);
    }

    private boolean hasRequiredSpecialization(TeacherBaseInfoRequest teacher, Long categoryId) {
        if (categoryId == null) {
            return true;
        }
        return teacher.getSpecializations() != null
                && teacher.getSpecializations().stream()
                .map(SpecializationsBaseDto::getId)
                .anyMatch(categoryId::equals);
    }

    private void validateLessonDate(LocalDate lessonDate, LocalTime endTime) {
        if (lessonDate == null) {
            throw new IllegalArgumentException("Lesson date is required");
        }
        if (lessonAlreadyEnded(lessonDate, endTime)) {
            throw new IllegalArgumentException("Replacement cannot be created for a past lesson");
        }
    }

    private boolean lessonAlreadyEnded(LocalDate lessonDate, LocalTime endTime) {
        LocalDateTime lessonEnd = LocalDateTime.of(lessonDate, endTime == null ? LocalTime.MAX : endTime);
        return lessonEnd.isBefore(LocalDateTime.now());
    }

    private WeekDays weekdayFromLessonDate(LocalDate lessonDate) {
        DayOfWeek day = lessonDate.getDayOfWeek();
        return switch (day) {
            case MONDAY -> WeekDays.MON;
            case TUESDAY -> WeekDays.TUE;
            case WEDNESDAY -> WeekDays.WED;
            case THURSDAY -> WeekDays.THU;
            case FRIDAY -> WeekDays.FRI;
            case SATURDAY -> WeekDays.SAT;
            case SUNDAY -> WeekDays.SUN;
        };
    }

    private void publishStatusChanged(ReplacementRequest request,
                                      GroupCourseBaseInfoRequest groupCourse,
                                      ScheduleBaseDtoRequest schedule,
                                      String message) {
        eventProducer.publishReplacementStatusChanged(new ReplacementStatusChangedEvent(
                request.getId(),
                request.getTeacherRequested(),
                request.getStatus().name(),
                groupCourse.getCourseName(),
                groupCourse.getGroupName(),
                request.getLessonDate(),
                schedule.getStartTime(),
                schedule.getEndTime(),
                message
        ));
    }

    private ReplacementRequestBaseDto enrich(ReplacementRequest replacementRequest) {
        ReplacementRequestBaseDto result = ReplacementMapper.mapToBaseDto(replacementRequest);
        List<ReplacementResponse> responses = responseRepository.findByReplacementRequestId(replacementRequest.getId());

        TeacherBaseInfoRequest requested = userClient.getTeacher(replacementRequest.getTeacherRequested());
        TeacherBaseInfoRequest approved = null;
        if (replacementRequest.getApprovedById() != null) {
            approved = userClient.getTeacher(replacementRequest.getApprovedById());
        }
        ScheduleBaseDtoRequest scheduleRequest = scheduleClient.getSchedule(replacementRequest.getScheduleId());
        GroupCourseBaseInfoRequest groupCourseBaseInfoRequest = groupCourseClient.groupCourseBaseInfoRequest(replacementRequest.getGroupCourseId());

        result.setTeacherBaseInfoRequest(requested);
        result.setApprovedByTeacherBaseInfoRequest(approved);
        result.setScheduleBaseDtoRequest(scheduleRequest);
        result.setGroupCourseBaseInfoRequest(groupCourseBaseInfoRequest);
        result.setPendingInvitationsCount((int) responses.stream()
                .filter(response -> response.getResponseStatus() == ResponseStatus.PENDING)
                .count());

        return result;
    }
}
