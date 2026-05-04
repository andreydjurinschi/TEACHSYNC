package com.teachsync.service;

import com.teachsync.domain.ReplacementRequest;
import com.teachsync.domain.ReplacementResponse;
import com.teachsync.domain.ResponseStatus;
import com.teachsync.domain.Status;
import com.teachsync.dto_s.replacementRequest.ReplacementRequestBaseDto;
import com.teachsync.dto_s.replacementRequest.ReplacementRequestCreateDto;
import com.teachsync.dto_s.statistics.ReplacementStatisticsDto;
import com.teachsync.dto_s.statistics.TeacherHelpMetricDto;
import com.teachsync.dto_s.statistics.TeacherReplacementStatisticsDto;
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
import com.teachsync.teachsyncevents.system.SystemAlertEvent;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
public class ReplacementRequestService {

    private static final Set<Status> ACTIVE_STATUSES = Set.of(Status.PENDING, Status.APPROVED);
    private static final Set<Status> PROBLEMATIC_STATUSES = Set.of(Status.PENDING, Status.EXPIRED, Status.AUTO_CLOSED);
    private static final String REPLACEMENTS_URL = "/profile/replacements";

    private final ReplacementRequestRepository repository;
    private final ReplacementResponseRepository responseRepository;
    private final ScheduleClient scheduleClient;
    private final GroupCourseClient groupCourseClient;
    private final UserClient userClient;
    private final ReferenceDataCacheService referenceDataCacheService;
    private final ReplacementEventProducer eventProducer;

    public ReplacementRequestService(ReplacementRequestRepository repository,
                                     ReplacementResponseRepository responseRepository,
                                     ScheduleClient scheduleClient,
                                     GroupCourseClient groupCourseClient,
                                     UserClient userClient,
                                     ReferenceDataCacheService referenceDataCacheService,
                                     ReplacementEventProducer eventProducer) {
        this.repository = repository;
        this.responseRepository = responseRepository;
        this.scheduleClient = scheduleClient;
        this.groupCourseClient = groupCourseClient;
        this.userClient = userClient;
        this.referenceDataCacheService = referenceDataCacheService;
        this.eventProducer = eventProducer;
    }

    @Transactional
    public ReplacementRequestBaseDto create(ReplacementRequestCreateDto dto) {
        ScheduleBaseDtoRequest schedule = requireDependency(
                "Создание заявки на замену: получение расписания",
                "schedule-service",
                () -> scheduleClient.getSchedule(dto.getScheduleId())
        );
        GroupCourseBaseInfoRequest groupCourse = requireDependency(
                "Создание заявки на замену: получение курса и группы",
                "course-service",
                () -> groupCourseClient.groupCourseBaseInfoRequest(schedule.getGroupCourseDto().getId())
        );
        WeekDays lessonWeekDay = weekdayFromLessonDate(dto.getLessonDate());
        Long teacherRequested = dto.getTeacherRequested() != null
                ? dto.getTeacherRequested()
                : schedule.getTeacherDto().getId();

        validateLessonDate(dto.getLessonDate(), schedule, lessonWeekDay);
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

        List<TeacherBaseInfoRequest> candidates = findCandidateTeachers(saved, schedule, groupCourse, lessonWeekDay);
        if (candidates.isEmpty()) {
            saved.setStatus(Status.AUTO_CLOSED);
            publishStatusChangedToRequester(
                    saved,
                    groupCourse,
                    schedule,
                    "Свободные преподаватели не найдены",
                    "Система не нашла свободных преподавателей с подходящей специализацией.",
                    REPLACEMENTS_URL
            );
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

        publishStatusChangedToRequester(
                saved,
                groupCourse,
                schedule,
                "Заявка на замену создана",
                "Запрос на замену отправлен подходящим преподавателям.",
                REPLACEMENTS_URL
        );

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

    public ReplacementStatisticsDto getStatistics() {
        return new ReplacementStatisticsDto(
                repository.count(),
                repository.countByStatus(Status.PENDING),
                repository.countByStatus(Status.APPROVED),
                repository.countByStatus(Status.DECLINED),
                repository.countByStatus(Status.EXPIRED),
                repository.countByStatus(Status.CANCELLED),
                repository.countByStatus(Status.AUTO_CLOSED),
                getTopHelpers()
        );
    }

    public TeacherReplacementStatisticsDto getTeacherStatistics(Long teacherId) {
        return new TeacherReplacementStatisticsDto(
                repository.countByTeacherRequested(teacherId),
                repository.countByApprovedById(teacherId),
                responseRepository.countByTeacherResponseAndResponseStatus(teacherId, ResponseStatus.PENDING),
                responseRepository.countByTeacherResponseAndResponseStatus(teacherId, ResponseStatus.DECLINED)
        );
    }

    @Transactional
    public void deleteRequest(Long requestId) {
        ReplacementRequest request = repository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Replacement request not found"));
        responseRepository.deleteByReplacementRequestId(request.getId());
        repository.delete(request);
    }

    @Transactional
    public void deleteByScheduleId(Long scheduleId) {
        List<ReplacementRequest> requests = repository.findByScheduleId(scheduleId);
        for (ReplacementRequest request : requests) {
            responseRepository.deleteByReplacementRequestId(request.getId());
        }
        repository.deleteAll(requests);
    }

    @Transactional
    public ReplacementRequestBaseDto approve(Long requestId, Long teacherId) {
        ReplacementRequest request = getPendingRequest(requestId);
        ReplacementResponse response = responseRepository
                .findByReplacementRequestIdAndTeacherResponse(requestId, teacherId)
                .orElseThrow(() -> new IllegalArgumentException("Teacher was not invited to this replacement"));

        ScheduleBaseDtoRequest schedule = requireDependency(
                "Подтверждение замены: получение расписания",
                "schedule-service",
                () -> scheduleClient.getSchedule(request.getScheduleId())
        );
        GroupCourseBaseInfoRequest groupCourse = requireDependency(
                "Подтверждение замены: получение курса и группы",
                "course-service",
                () -> groupCourseClient.groupCourseBaseInfoRequest(request.getGroupCourseId())
        );
        TeacherBaseInfoRequest teacher = requireDependency(
                "Подтверждение замены: проверка преподавателя",
                "users-service",
                () -> userClient.getTeacher(teacherId)
        );

        if (!isTeacherAvailableForSchedule(schedule, request, teacherId) || !hasRequiredSpecialization(teacher, groupCourse.getCategoryId())) {
            throw new IllegalStateException("Teacher is no longer available or does not match course category");
        }

        List<Long> otherInvitedTeachers = responseRepository.findByReplacementRequestId(requestId).stream()
                .map(ReplacementResponse::getTeacherResponse)
                .filter(otherTeacherId -> !teacherId.equals(otherTeacherId))
                .toList();

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
        publishStatusChangedToInvitedTeachers(
                request,
                groupCourse,
                schedule,
                otherInvitedTeachers,
                "Заявка на замену закрыта",
                "Замена для этой пары уже подтверждена другим преподавателем.",
                REPLACEMENTS_URL
        );

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
        List<Long> invitedTeachers = responseRepository.findByReplacementRequestId(requestId).stream()
                .map(ReplacementResponse::getTeacherResponse)
                .toList();
        responseRepository.findByReplacementRequestId(requestId)
                .forEach(item -> item.setResponseStatus(ResponseStatus.DECLINED));

        ScheduleBaseDtoRequest schedule = requireDependency(
                "Отмена заявки на замену: получение расписания",
                "schedule-service",
                () -> scheduleClient.getSchedule(request.getScheduleId())
        );
        GroupCourseBaseInfoRequest groupCourse = requireDependency(
                "Отмена заявки на замену: получение курса и группы",
                "course-service",
                () -> groupCourseClient.groupCourseBaseInfoRequest(request.getGroupCourseId())
        );
        publishStatusChangedToRequester(
                request,
                groupCourse,
                schedule,
                "Заявка на замену отменена",
                "Вы отменили заявку на замену.",
                REPLACEMENTS_URL
        );
        publishStatusChangedToInvitedTeachers(
                request,
                groupCourse,
                schedule,
                invitedTeachers,
                "Заявка на замену закрыта",
                "Запрос на замену был отменен преподавателем, который создал заявку.",
                REPLACEMENTS_URL
        );
        return enrich(request);
    }

    @Scheduled(fixedDelay = 300000)
    @Transactional
    public void expireStaleRequests() {
        for (ReplacementRequest request : repository.findByStatus(Status.PENDING)) {
            ScheduleBaseDtoRequest schedule = requireDependency(
                    "Автоматическое закрытие просроченных замен: получение расписания",
                    "schedule-service",
                    () -> scheduleClient.getSchedule(request.getScheduleId())
            );
            if (!lessonAlreadyEnded(request.getLessonDate(), schedule.getEndTime())) {
                continue;
            }

            request.setStatus(Status.EXPIRED);
            List<Long> invitedTeachers = responseRepository.findByReplacementRequestId(request.getId()).stream()
                    .map(ReplacementResponse::getTeacherResponse)
                    .toList();
            responseRepository.findByReplacementRequestId(request.getId())
                    .forEach(item -> item.setResponseStatus(ResponseStatus.DECLINED));

            GroupCourseBaseInfoRequest groupCourse = requireDependency(
                    "Автоматическое закрытие просроченных замен: получение курса и группы",
                    "course-service",
                    () -> groupCourseClient.groupCourseBaseInfoRequest(request.getGroupCourseId())
            );
            publishStatusChangedToRequester(
                    request,
                    groupCourse,
                    schedule,
                    "Заявка на замену просрочена",
                    "Время пары уже прошло, заявка автоматически закрыта.",
                    REPLACEMENTS_URL
            );
            publishStatusChangedToInvitedTeachers(
                    request,
                    groupCourse,
                    schedule,
                    invitedTeachers,
                    "Заявка на замену закрыта",
                    "Время пары уже прошло, запрос на замену больше неактуален.",
                    REPLACEMENTS_URL
            );
        }
    }

    private List<TeacherBaseInfoRequest> findCandidateTeachers(ReplacementRequest request,
                                                               ScheduleBaseDtoRequest schedule,
                                                               GroupCourseBaseInfoRequest groupCourse,
                                                               WeekDays lessonWeekDay) {
        Set<Long> freeTeacherIds = new HashSet<>(
                requireDependency(
                        "Поиск кандидатов на замену: получение свободных преподавателей",
                        "schedule-service",
                        () -> scheduleClient.getAvailableTeachers(schedule.getId(), lessonWeekDay)
                )
        );
        if (freeTeacherIds.isEmpty()) {
            return List.of();
        }
        return requireDependency(
                        "Поиск кандидатов на замену: получение данных преподавателей",
                        "users-service",
                        () -> userClient.getByIds(List.copyOf(freeTeacherIds))
                )
                .stream()
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
        ScheduleBaseDtoRequest schedule = requireDependency(
                "Автозакрытие заявки на замену: получение расписания",
                "schedule-service",
                () -> scheduleClient.getSchedule(request.getScheduleId())
        );
        GroupCourseBaseInfoRequest groupCourse = requireDependency(
                "Автозакрытие заявки на замену: получение курса и группы",
                "course-service",
                () -> groupCourseClient.groupCourseBaseInfoRequest(request.getGroupCourseId())
        );
        publishStatusChangedToRequester(
                request,
                groupCourse,
                schedule,
                "Замена не найдена",
                "Все приглашенные преподаватели отказались или не подтвердили замену.",
                REPLACEMENTS_URL
        );
    }

    private boolean isTeacherAvailableForSchedule(ScheduleBaseDtoRequest schedule,
                                                  ReplacementRequest request,
                                                  Long teacherId) {
        return requireDependency(
                "Подтверждение замены: повторная проверка доступности преподавателя",
                "schedule-service",
                () -> scheduleClient.getAvailableTeachers(schedule.getId(), weekdayFromLessonDate(request.getLessonDate()))
        )
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

    private void validateLessonDate(LocalDate lessonDate, ScheduleBaseDtoRequest schedule, WeekDays lessonWeekDay) {
        if (lessonDate == null) {
            throw new IllegalArgumentException("Lesson date is required");
        }
        if (schedule.getWeekDays() == null || !schedule.getWeekDays().contains(lessonWeekDay)) {
            throw new IllegalArgumentException("Replacement can be created only for an actual lesson date from the schedule");
        }
        if (lessonAlreadyEnded(lessonDate, schedule.getEndTime())) {
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
                                      Long targetTeacherId,
                                      String title,
                                      String message,
                                      String actionUrl) {
        eventProducer.publishReplacementStatusChanged(new ReplacementStatusChangedEvent(
                request.getId(),
                request.getTeacherRequested(),
                request.getStatus().name(),
                groupCourse.getCourseName(),
                groupCourse.getGroupName(),
                request.getLessonDate(),
                schedule.getStartTime(),
                schedule.getEndTime(),
                targetTeacherId,
                title,
                message,
                actionUrl
        ));
    }

    private void publishStatusChangedToRequester(ReplacementRequest request,
                                                 GroupCourseBaseInfoRequest groupCourse,
                                                 ScheduleBaseDtoRequest schedule,
                                                 String title,
                                                 String message,
                                                 String actionUrl) {
        publishStatusChanged(request, groupCourse, schedule, request.getTeacherRequested(), title, message, actionUrl);
    }

    private void publishStatusChangedToInvitedTeachers(ReplacementRequest request,
                                                       GroupCourseBaseInfoRequest groupCourse,
                                                       ScheduleBaseDtoRequest schedule,
                                                       List<Long> teacherIds,
                                                       String title,
                                                       String message,
                                                       String actionUrl) {
        teacherIds.stream()
                .distinct()
                .filter(teacherId -> !teacherId.equals(request.getTeacherRequested()))
                .forEach(teacherId -> publishStatusChanged(
                        request,
                        groupCourse,
                        schedule,
                        teacherId,
                        title,
                        message,
                        actionUrl
                ));
    }

    private ReplacementRequestBaseDto enrich(ReplacementRequest replacementRequest) {
        ReplacementRequestBaseDto result = ReplacementMapper.mapToBaseDto(replacementRequest);
        List<ReplacementResponse> responses = responseRepository.findByReplacementRequestId(replacementRequest.getId());

        TeacherBaseInfoRequest requested = referenceDataCacheService.getTeacher(replacementRequest.getTeacherRequested());
        TeacherBaseInfoRequest approved = null;
        if (replacementRequest.getApprovedById() != null) {
            approved = referenceDataCacheService.getTeacher(replacementRequest.getApprovedById());
        }
        ScheduleBaseDtoRequest scheduleRequest = referenceDataCacheService.getSchedule(replacementRequest.getScheduleId());
        GroupCourseBaseInfoRequest groupCourseBaseInfoRequest = referenceDataCacheService.getGroupCourse(replacementRequest.getGroupCourseId());

        result.setTeacherBaseInfoRequest(requested);
        result.setApprovedByTeacherBaseInfoRequest(approved);
        result.setScheduleBaseDtoRequest(scheduleRequest);
        result.setGroupCourseBaseInfoRequest(groupCourseBaseInfoRequest);
        result.setPendingInvitationsCount((int) responses.stream()
                .filter(response -> response.getResponseStatus() == ResponseStatus.PENDING)
                .count());

        return result;
    }

    private List<TeacherHelpMetricDto> getTopHelpers() {
        Map<Long, Long> approvedByTeacher = repository.findAll().stream()
                .filter(request -> request.getApprovedById() != null)
                .collect(Collectors.groupingBy(ReplacementRequest::getApprovedById, Collectors.counting()));

        if (approvedByTeacher.isEmpty()) {
            return List.of();
        }

        List<Long> teacherIds = approvedByTeacher.entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .limit(5)
                .map(Map.Entry::getKey)
                .toList();

        Map<Long, TeacherBaseInfoRequest> teachers = referenceDataCacheService.getTeachersByIds(teacherIds).stream()
                .collect(Collectors.toMap(TeacherBaseInfoRequest::getId, Function.identity()));

        return teacherIds.stream()
                .map(teacherId -> {
                    TeacherBaseInfoRequest teacher = teachers.get(teacherId);
                    return new TeacherHelpMetricDto(
                            teacherId,
                            teacher == null ? "Преподаватель #" + teacherId : teacher.displayName(),
                            approvedByTeacher.getOrDefault(teacherId, 0L),
                            responseRepository.countByTeacherResponseAndResponseStatus(teacherId, ResponseStatus.DECLINED),
                            responseRepository.countByTeacherResponseAndResponseStatus(teacherId, ResponseStatus.PENDING)
                    );
                })
                .sorted(Comparator.comparingLong(TeacherHelpMetricDto::approvedReplacements).reversed())
                .toList();
    }

    private <T> T requireDependency(String operation, String dependency, Supplier<T> call) {
        try {
            return call.get();
        } catch (RuntimeException e) {
            publishDependencyAlert(operation, dependency, e);
            throw e;
        }
    }

    private void publishDependencyAlert(String operation, String dependency, RuntimeException e) {
        eventProducer.publishSystemAlert(new SystemAlertEvent(
                "replacement-service",
                operation,
                dependency,
                "HIGH",
                "Операция не может быть безопасно выполнена без актуальных данных зависимого сервиса",
                e.getClass().getSimpleName() + ": " + (e.getMessage() == null ? "" : e.getMessage())
        ));
    }
}
