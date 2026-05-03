package com.teachsync.services;

import com.teachsync.domain.ClassRoom;
import com.teachsync.domain.Schedule;
import com.teachsync.domain.ScheduleDay;
import com.teachsync.domain.WeekDays;
import com.teachsync.dto_s.domain.class_room.ClassRoomBaseDto;
import com.teachsync.dto_s.domain.schedule.ScheduleBaseDto;
import com.teachsync.dto_s.domain.schedule.ScheduleCreateDto;
import com.teachsync.dto_s.domain.schedule.ScheduleUpdateDto;
import com.teachsync.dto_s.domain.statistics.ScheduleStatisticsDto;
import com.teachsync.dto_s.domain.statistics.TeacherWorkloadStatisticsDto;
import com.teachsync.dto_s.feign.GroupCourseDto;
import com.teachsync.exceptions.InvalidTimeRangeException;
import com.teachsync.exceptions.ScheduleConflictException;
import com.teachsync.interation.feign.Role;
import com.teachsync.interation.feign.clients.GroupCourseClient;
import com.teachsync.interation.feign.clients.TeacherClient;
import com.teachsync.interation.feign.requests.GroupCourseBaseInfoRequest;
import com.teachsync.interation.feign.requests.TeacherBaseInfoRequest;
import com.teachsync.interation.kafka.ScheduleEventProducer;
import com.teachsync.mappers.schedule.ScheduleMapper;
import com.teachsync.repositories.ClassRoomRepository;
import com.teachsync.repositories.ScheduleDayRepository;
import com.teachsync.repositories.ScheduleRepository;
import com.teachsync.teachsyncevents.schedules.ScheduleCreatedEvent;
import com.teachsync.teachsyncevents.schedules.ScheduleUpdatedEvent;
import com.teachsync.teachsyncevents.system.SystemAlertEvent;
import com.teachsync.validator.CustomTimeValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class ScheduleService {

    private final Logger log = Logger.getLogger(ScheduleService.class.getName());

    private final ScheduleRepository scheduleRepository;
    private final ClassRoomRepository classRoomRepository;
    private final CustomTimeValidator timeValidator;
    private final TeacherClient teacherClient;
    private final GroupCourseClient groupCourseClient;
    private final ReferenceDataCacheService referenceDataCacheService;
    private final ScheduleDayRepository scheduleDayRepository;
    private final ScheduleEventProducer scheduleEventProducer;

    public ScheduleService(ScheduleRepository scheduleRepository, ClassRoomRepository classRoomRepository, CustomTimeValidator timeValidator, TeacherClient teacherClient, GroupCourseClient groupCourseClient, ReferenceDataCacheService referenceDataCacheService, ScheduleDayRepository scheduleDayRepository, ScheduleEventProducer scheduleEventProducer) {
        this.scheduleRepository = scheduleRepository;
        this.classRoomRepository = classRoomRepository;
        this.timeValidator = timeValidator;
        this.teacherClient = teacherClient;
        this.groupCourseClient = groupCourseClient;
        this.referenceDataCacheService = referenceDataCacheService;
        this.scheduleDayRepository = scheduleDayRepository;
        this.scheduleEventProducer = scheduleEventProducer;
    }

    public List<ScheduleBaseDto> getAll() {
        List<Schedule> all = scheduleRepository.findWithClassRooms();

        List<Long> teacherIds    = all.stream().map(Schedule::getTeacherId).distinct().toList();
        List<Long> groupCourseIds = all.stream().map(Schedule::getGroupCourseId).distinct().toList();

        Map<Long, TeacherBaseInfoRequest> teacherMap = referenceDataCacheService
                .getTeachersByIds(teacherIds)
                .stream()
                .collect(Collectors.toMap(TeacherBaseInfoRequest::getId, t -> t));

        Map<Long, GroupCourseBaseInfoRequest> groupCourseMap = referenceDataCacheService
                .getGroupCoursesByIds(groupCourseIds)
                .stream()
                .collect(Collectors.toMap(GroupCourseBaseInfoRequest::getId, g -> g));

        return all.stream().map(schedule -> {
            ScheduleBaseDto dto = ScheduleMapper.mapToBaseDto(schedule);
            dto.setTeacherDto(teacherMap.get(schedule.getTeacherId()));
            dto.setGroupCourseDto(groupCourseMap.get(schedule.getGroupCourseId()));
            return dto;
        }).toList();
    }

    public List<ScheduleBaseDto> getScheduleForTeacher(Long teacherId) {
        List<Schedule> schedules = scheduleRepository.findAllForTeacher(teacherId);

        log.info("Schedules for " + teacherId + ": " + schedules);

        List<Long> groupCourseIds = schedules.stream()
                .map(Schedule::getGroupCourseId)
                .distinct()
                .toList();

        Map<Long, GroupCourseBaseInfoRequest> groupCourseMap = referenceDataCacheService
                .getGroupCoursesByIds(groupCourseIds)
                .stream()
                .collect(Collectors.toMap(GroupCourseBaseInfoRequest::getId, g -> g));

        return schedules.stream().map(schedule -> {
            ScheduleBaseDto dto = ScheduleMapper.mapToBaseDto(schedule);
            dto.setGroupCourseDto(groupCourseMap.get(schedule.getGroupCourseId()));
            return dto;
        }).toList();
    }

    public List<ClassRoomBaseDto> getAllClassrooms() {
        return classRoomRepository.findAll()
                .stream()
                .map(cr -> new ClassRoomBaseDto(cr.getId(), cr.getName(), cr.getCapacity()))
                .toList();
    }

    public List<TeacherBaseInfoRequest> getAllTeachers(Role role){
        return referenceDataCacheService.getAllTeachers(role);
    }

    public  List<GroupCourseBaseInfoRequest> getAllGroupCourses(){
        return referenceDataCacheService.getAllGroupCourses();
    }

    public ScheduleStatisticsDto getStatistics() {
        long totalGroupCourses = referenceDataCacheService.getAllGroupCourses().size();
        long scheduledGroupCourses = scheduleRepository.countScheduledGroupCourses();
        return new ScheduleStatisticsDto(
                scheduleRepository.count(),
                scheduledGroupCourses,
                Math.max(0, totalGroupCourses - scheduledGroupCourses),
                totalGroupCourses,
                classRoomRepository.count()
        );
    }

    public TeacherWorkloadStatisticsDto getTeacherWorkload(Long teacherId) {
        List<Schedule> schedules = scheduleRepository.findAllForTeacher(teacherId);
        long weeklyLessons = schedules.stream()
                .mapToLong(schedule -> schedule.getWeekDays() == null ? 0 : schedule.getWeekDays().size())
                .sum();
        long weeklyMinutes = schedules.stream()
                .mapToLong(schedule -> Duration.between(schedule.getStartTime(), schedule.getEndTime()).toMinutes()
                        * (schedule.getWeekDays() == null ? 0 : schedule.getWeekDays().size()))
                .sum();
        long scheduledGroupCourses = schedules.stream()
                .map(Schedule::getGroupCourseId)
                .filter(Objects::nonNull)
                .distinct()
                .count();

        return new TeacherWorkloadStatisticsDto(
                weeklyLessons,
                Math.round(weeklyMinutes / 60.0),
                scheduledGroupCourses,
                scheduledGroupCourses
        );
    }

    public ScheduleBaseDto getById(Long id){
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("this schedule does not exist"));
        ScheduleBaseDto dto = ScheduleMapper.mapToBaseDto(schedule);
        dto.setTeacherDto(referenceDataCacheService.getTeacher(schedule.getTeacherId()));
        dto.setGroupCourseDto(referenceDataCacheService.getGroupCourse(schedule.getGroupCourseId()));
        return dto;
    }

    public List<GroupCourseBaseInfoRequest> getAllGroupCoursesWithoutSchedule(){
        List<GroupCourseBaseInfoRequest> allGroupCourses = referenceDataCacheService.getAllGroupCourses();

        log.info("allGroupCourses: [ " + allGroupCourses.stream().map(GroupCourseBaseInfoRequest::getId).toList() + " ]");

        List<Long> notedGroupCoursesInSchedules = scheduleRepository.findAll().stream().map(Schedule::getGroupCourseId).toList();

        log.info("noted courses: [ " + notedGroupCoursesInSchedules.size() + " ]");

        var result = new ArrayList<GroupCourseBaseInfoRequest>();
        for(GroupCourseBaseInfoRequest item : allGroupCourses){
            if(!notedGroupCoursesInSchedules.contains(item.getId())){
                result.add(item);
            }
        }

        log.warning("group courses not mentioned in schedule : [ " + result.stream().map(GroupCourseBaseInfoRequest::getId).sorted().toList() + " ]");

        return result;
    }

    public List<Long> findAvailableTeachers(Long scheduleId, WeekDays weekDays) {

        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow();
        if (!schedule.getWeekDays().contains(weekDays)) {
            throw new IllegalArgumentException("Selected lesson date does not belong to this schedule");
        }

        LocalTime start = schedule.getStartTime();
        LocalTime end = schedule.getEndTime();

        List<Schedule> conflicts = scheduleRepository.findConflictingSchedules(weekDays.name(), start, end);

        Set<Long> busyTeachers = conflicts.stream().map(Schedule::getTeacherId).collect(Collectors.toSet());

        List<TeacherBaseInfoRequest> allTeachers = referenceDataCacheService.getAllTeachers(Role.TEACHER);
        List<TeacherBaseInfoRequest> availableTeachers = allTeachers.stream().filter(t -> !busyTeachers.contains(t.getId())).toList();

        System.out.println("сообщение будет сгенерировано следующим учителям:");
        availableTeachers.forEach(t -> {
            System.out.println(t.getName() + " " + t.getEmail());
        });
        return availableTeachers.stream().map(TeacherBaseInfoRequest::getId).toList();
    }

    @Transactional
    public ScheduleBaseDto update(Long id, ScheduleUpdateDto dto, Long changedByUserId) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("this schedule does not exist"));

        Long oldTeacherId = schedule.getTeacherId();
        Long oldGroupCourseId = schedule.getGroupCourseId();
        LocalTime oldStartTime = schedule.getStartTime();
        LocalTime oldEndTime = schedule.getEndTime();
        Set<WeekDays> oldWeekDays = new HashSet<>(schedule.getWeekDays());
        String oldClassRoomName = schedule.getClassRoom() == null ? "не указана" : schedule.getClassRoom().getName();

        LocalTime newStartTime = dto.getStartTime() == null ? schedule.getStartTime() : dto.getStartTime();
        LocalTime newEndTime = dto.getEndTime() == null ? schedule.getEndTime() : dto.getEndTime();
        Set<WeekDays> newWeekDays = dto.getWeekDays() == null || dto.getWeekDays().isEmpty()
                ? new HashSet<>(schedule.getWeekDays())
                : new HashSet<>(dto.getWeekDays());
        Long newGroupCourseId = dto.getGroupCourseId() == null ? schedule.getGroupCourseId() : dto.getGroupCourseId();
        Long newClassRoomId = dto.getClassRoomId() == null ? schedule.getClassRoom().getId() : dto.getClassRoomId();

        validateTime(newStartTime, newEndTime);

        ClassRoom classRoom = classRoomRepository.findById(newClassRoomId)
                .orElseThrow(() -> new NoSuchElementException("classroom does not exist"));
        GroupCourseBaseInfoRequest groupCourse = requireDependency(
                "Обновление расписания",
                "course-service",
                () -> groupCourseClient.groupCourseBaseInfoRequest(newGroupCourseId)
        );
        if (groupCourse.getTeacherId() == null) {
            throw new IllegalArgumentException("Нельзя назначить расписание на курс без преподавателя");
        }

        GroupCourseDto groupSize = requireDependency(
                "Проверка вместимости аудитории при обновлении расписания",
                "course-service",
                () -> groupCourseClient.getGroupSizeInformation(newGroupCourseId)
        );
        if (classRoom.getCapacity() < groupSize.getCapacity()) {
            throw new IllegalArgumentException(
                    "Аудитория вмещает " + classRoom.getCapacity() +
                            " студентов, а в группе " + groupSize.getCapacity()
            );
        }

        findClassRoomConflicts(id, newWeekDays, newStartTime, newEndTime, classRoom);
        findTeacherConflicts(id, newWeekDays, newStartTime, newEndTime, groupCourse.getTeacherId());
        findGroupConflicts(id, newWeekDays, newStartTime, newEndTime, groupCourse);

        schedule.setStartTime(newStartTime);
        schedule.setEndTime(newEndTime);
        schedule.setWeekDays(newWeekDays);
        schedule.setGroupCourseId(newGroupCourseId);
        schedule.setTeacherId(groupCourse.getTeacherId());
        schedule.setClassRoom(classRoom);

        Schedule saved = scheduleRepository.save(schedule);
        TeacherBaseInfoRequest changedBy = resolveUser(changedByUserId);
        String changedByName = displayName(changedBy, changedByUserId);
        String changeSummary = buildChangeSummary(
                oldStartTime,
                oldEndTime,
                oldWeekDays,
                oldGroupCourseId,
                oldTeacherId,
                oldClassRoomName,
                saved,
                groupCourse,
                classRoom
        );

        scheduleEventProducer.publishScheduleUpdated(new ScheduleUpdatedEvent(
                saved.getId(),
                oldTeacherId,
                saved.getTeacherId(),
                saved.getGroupCourseId(),
                groupCourse.getCourseName(),
                groupCourse.getGroupName(),
                classRoom.getName(),
                saved.getStartTime(),
                saved.getEndTime(),
                saved.getWeekDays().stream().map(Enum::name).collect(Collectors.toSet()),
                changedByUserId,
                changedByName,
                changeSummary
        ));

        ScheduleBaseDto result = ScheduleMapper.mapToBaseDto(saved);
        result.setTeacherDto(referenceDataCacheService.getTeacher(saved.getTeacherId()));
        result.setGroupCourseDto(groupCourse);
        return result;
    }

    public ScheduleBaseDto create(ScheduleCreateDto dto) {
        ClassRoom classRoom = classRoomRepository.findById(dto.getClassRoomId())
                .orElseThrow();

        GroupCourseBaseInfoRequest groupCourse = requireDependency(
                "Создание расписания",
                "course-service",
                () -> groupCourseClient.groupCourseBaseInfoRequest(dto.getGroupCourseId())
        );
        if (groupCourse.getTeacherId() == null) {
            throw new IllegalArgumentException("Нельзя создать расписание для курса без преподавателя");
        }

        GroupCourseDto groupSize = requireDependency(
                "Проверка вместимости аудитории при создании расписания",
                "course-service",
                () -> groupCourseClient.getGroupSizeInformation(dto.getGroupCourseId())
        );
        if (classRoom.getCapacity() < groupSize.getCapacity()) {
            throw new IllegalArgumentException(
                    "Аудитория вмещает " + classRoom.getCapacity() +
                            " студентов, а в группе " + groupSize.getCapacity()
            );
        }

        findClassRoomConflicts(dto, classRoom);
        findTeacherConflicts(dto, groupCourse.getTeacherId());
        // todo
        //findGroupCourseConflicts(dto, groupCourse);

        Schedule schedule = new Schedule(
                dto.getStartTime(), dto.getEndTime(),
                dto.getWeekDays(),
                dto.getGroupCourseId(),
                groupCourse.getTeacherId(),
                classRoom
        );

        Schedule saved = scheduleRepository.save(schedule);
        scheduleEventProducer.publishScheduleCreated(new ScheduleCreatedEvent(
                saved.getId(),
                saved.getTeacherId(),
                saved.getGroupCourseId(),
                groupCourse.getCourseName(),
                groupCourse.getGroupName(),
                classRoom.getName(),
                saved.getStartTime(),
                saved.getEndTime(),
                saved.getWeekDays().stream().map(Enum::name).collect(Collectors.toSet())
        ));

        return ScheduleMapper.mapToBaseDto(saved);
    }


    //TODO: schedule delete logic
    public void delete(Long id){

    }

    private void findTeacherConflicts(ScheduleCreateDto dto, Long teacherId) {
        List<Schedule> teacherConflicts = scheduleRepository.findTeacherConflicts(
                dto.getWeekDays(),
                dto.getStartTime(),
                dto.getEndTime(),
                teacherId
        );
        if(!teacherConflicts.isEmpty()){
            TeacherBaseInfoRequest teacherBaseInfoRequest = referenceDataCacheService.getTeacher(teacherId);
            throw new ScheduleConflictException(
                    "Преподаватель «" + teacherBaseInfoRequest.getName() + " " + teacherBaseInfoRequest.getSurname() + "» уже занят в это время"
            );
        }
    }

    private void findTeacherConflicts(Long scheduleId,
                                      Set<WeekDays> days,
                                      LocalTime startTime,
                                      LocalTime endTime,
                                      Long teacherId) {
        List<Schedule> teacherConflicts = scheduleRepository.findTeacherConflicts(
                days,
                startTime,
                endTime,
                teacherId
        ).stream().filter(item -> !item.getId().equals(scheduleId)).toList();
        if (!teacherConflicts.isEmpty()) {
            TeacherBaseInfoRequest teacherBaseInfoRequest = referenceDataCacheService.getTeacher(teacherId);
            throw new ScheduleConflictException(
                    "Преподаватель «" + teacherBaseInfoRequest.getName() + " " + teacherBaseInfoRequest.getSurname() + "» уже занят в это время"
            );
        }
    }


    private void findClassRoomConflicts(ScheduleCreateDto dto, ClassRoom classRoom) {
        List<Schedule> classRoomConflicts = scheduleRepository.findClassRoomConflicts(
                dto.getWeekDays(),
                dto.getStartTime(),
                dto.getEndTime(),
                dto.getClassRoomId()
        );
        if (!classRoomConflicts.isEmpty()) {
            throw new ScheduleConflictException(
                    "Кабинет «" + classRoom.getName() + "» уже занят в это время"
            );
        }
    }

    private void findClassRoomConflicts(Long scheduleId,
                                        Set<WeekDays> days,
                                        LocalTime startTime,
                                        LocalTime endTime,
                                        ClassRoom classRoom) {
        List<Schedule> classRoomConflicts = scheduleRepository.findClassRoomConflicts(
                days,
                startTime,
                endTime,
                classRoom.getId()
        ).stream().filter(item -> !item.getId().equals(scheduleId)).toList();
        if (!classRoomConflicts.isEmpty()) {
            throw new ScheduleConflictException(
                    "Кабинет «" + classRoom.getName() + "» уже занят в это время"
            );
        }
    }

    private void findGroupConflicts(Long scheduleId,
                                    Set<WeekDays> days,
                                    LocalTime startTime,
                                    LocalTime endTime,
                                    GroupCourseBaseInfoRequest targetGroupCourse) {
        List<Schedule> conflicts = scheduleRepository.findAllConflictingSchedules(days, startTime, endTime)
                .stream()
                .filter(item -> !item.getId().equals(scheduleId))
                .toList();
        if (conflicts.isEmpty()) {
            return;
        }

        List<Long> groupCourseIds = conflicts.stream()
                .map(Schedule::getGroupCourseId)
                .distinct()
                .toList();
        Map<Long, GroupCourseBaseInfoRequest> groupCourseMap = requireDependency(
                "Проверка конфликта группы при обновлении расписания",
                "course-service",
                () -> groupCourseClient.getGroupCoursesByIds(groupCourseIds)
        )
                .stream()
                .collect(Collectors.toMap(GroupCourseBaseInfoRequest::getId, item -> item));

        boolean groupBusy = conflicts.stream()
                .map(item -> groupCourseMap.get(item.getGroupCourseId()))
                .filter(Objects::nonNull)
                .anyMatch(item -> Objects.equals(item.getGroupId(), targetGroupCourse.getGroupId()));

        if (groupBusy) {
            throw new ScheduleConflictException(
                    "Группа «" + targetGroupCourse.getGroupName() + "» уже занята в это время"
            );
        }
    }

    private void validateTime(LocalTime startTime, LocalTime endTime) {
        try {
            timeValidator.checkTime(startTime, endTime);
        } catch (InvalidTimeRangeException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private TeacherBaseInfoRequest resolveUser(Long userId) {
        if (userId == null) {
            return null;
        }
        try {
            return referenceDataCacheService.getTeacher(userId);
        } catch (Exception e) {
            log.warning("Could not resolve user " + userId + " for schedule update notification");
            return null;
        }
    }

    private String displayName(TeacherBaseInfoRequest user, Long fallbackId) {
        if (user == null) {
            return "пользователь #" + fallbackId;
        }
        String fullName = (safe(user.getName()) + " " + safe(user.getSurname())).trim();
        if (!fullName.isBlank()) {
            return fullName;
        }
        return user.getEmail() == null ? "пользователь #" + fallbackId : user.getEmail();
    }

    private String buildChangeSummary(LocalTime oldStartTime,
                                      LocalTime oldEndTime,
                                      Set<WeekDays> oldWeekDays,
                                      Long oldGroupCourseId,
                                      Long oldTeacherId,
                                      String oldClassRoomName,
                                      Schedule saved,
                                      GroupCourseBaseInfoRequest newGroupCourse,
                                      ClassRoom newClassRoom) {
        List<String> changes = new ArrayList<>();
        if (!Objects.equals(oldStartTime, saved.getStartTime()) || !Objects.equals(oldEndTime, saved.getEndTime())) {
            changes.add("время: " + oldStartTime + "-" + oldEndTime + " -> " + saved.getStartTime() + "-" + saved.getEndTime());
        }
        if (!Objects.equals(oldWeekDays, saved.getWeekDays())) {
            changes.add("дни: " + formatDays(oldWeekDays) + " -> " + formatDays(saved.getWeekDays()));
        }
        if (!Objects.equals(oldGroupCourseId, saved.getGroupCourseId())) {
            changes.add("курс/группа: " + oldGroupCourseId + " -> "
                    + newGroupCourse.getGroupName() + " / " + newGroupCourse.getCourseName());
        }
        if (!Objects.equals(oldTeacherId, saved.getTeacherId())) {
            changes.add("преподаватель: " + oldTeacherId + " -> " + newGroupCourse.getTeacherName());
        }
        if (!Objects.equals(oldClassRoomName, newClassRoom.getName())) {
            changes.add("аудитория: " + oldClassRoomName + " -> " + newClassRoom.getName());
        }
        return changes.isEmpty() ? "без видимых изменений" : String.join("; ", changes);
    }

    private String formatDays(Set<WeekDays> days) {
        return days.stream().map(Enum::name).sorted().collect(Collectors.joining(", "));
    }

    private String safe(String value) {
        return value == null ? "" : value;
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
        scheduleEventProducer.publishSystemAlert(new SystemAlertEvent(
                "schedule-service",
                operation,
                dependency,
                "HIGH",
                "Операция не может быть безопасно выполнена без актуальных данных зависимого сервиса",
                e.getClass().getSimpleName() + ": " + safe(e.getMessage())
        ));
    }

    // todo
/*    private void findGroupCourseConflicts(ScheduleCreateDto dto, GroupCourseBaseInfoRequest groupCourseBaseInfoRequest) {

        List<Schedule> groupCourseConflicts = scheduleRepository.findGroupCourseConflicts(
                dto.getWeekDays(),
                dto.getStartTime(),
                dto.getEndTime(),
                groupCourseBaseInfoRequest.getGroupId()
        );
        if (!groupCourseConflicts.isEmpty()) {
            throw new ScheduleConflictException(
                    "Группа «" + groupCourseBaseInfoRequest.getGroupName() + "» уже занята в это время"
            );
        }
    }*/

}
