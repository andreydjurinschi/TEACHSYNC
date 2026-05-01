package com.teachsync.services;

import com.teachsync.domain.ClassRoom;
import com.teachsync.domain.Schedule;
import com.teachsync.domain.ScheduleDay;
import com.teachsync.domain.WeekDays;
import com.teachsync.dto_s.domain.class_room.ClassRoomBaseDto;
import com.teachsync.dto_s.domain.schedule.ScheduleBaseDto;
import com.teachsync.dto_s.domain.schedule.ScheduleCreateDto;
import com.teachsync.dto_s.domain.schedule.ScheduleUpdateDto;
import com.teachsync.dto_s.feign.GroupCourseDto;
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
import com.teachsync.validator.CustomTimeValidator;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.*;
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
    private final ScheduleDayRepository scheduleDayRepository;
    private final ScheduleEventProducer scheduleEventProducer;

    public ScheduleService(ScheduleRepository scheduleRepository, ClassRoomRepository classRoomRepository, CustomTimeValidator timeValidator, TeacherClient teacherClient, GroupCourseClient groupCourseClient, ScheduleDayRepository scheduleDayRepository, ScheduleEventProducer scheduleEventProducer) {
        this.scheduleRepository = scheduleRepository;
        this.classRoomRepository = classRoomRepository;
        this.timeValidator = timeValidator;
        this.teacherClient = teacherClient;
        this.groupCourseClient = groupCourseClient;
        this.scheduleDayRepository = scheduleDayRepository;
        this.scheduleEventProducer = scheduleEventProducer;
    }

    public List<ScheduleBaseDto> getAll() {
        List<Schedule> all = scheduleRepository.findWithClassRooms();

        List<Long> teacherIds    = all.stream().map(Schedule::getTeacherId).distinct().toList();
        List<Long> groupCourseIds = all.stream().map(Schedule::getGroupCourseId).distinct().toList();

        Map<Long, TeacherBaseInfoRequest> teacherMap = teacherClient
                .getTeachersByIds(teacherIds)
                .stream()
                .collect(Collectors.toMap(TeacherBaseInfoRequest::getId, t -> t));

        Map<Long, GroupCourseBaseInfoRequest> groupCourseMap = groupCourseClient
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

        Map<Long, GroupCourseBaseInfoRequest> groupCourseMap = groupCourseClient
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
        return teacherClient.getAllTeachers(role);
    }

    public  List<GroupCourseBaseInfoRequest> getAllGroupCourses(){
        return groupCourseClient.getAllGroupCourses();
    }

    public ScheduleBaseDto getById(Long id){
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("this schedule does not exist"));
        ScheduleBaseDto dto = ScheduleMapper.mapToBaseDto(schedule);
        dto.setTeacherDto(teacherClient.requestForUserFromUserService(schedule.getTeacherId()));
        dto.setGroupCourseDto(groupCourseClient.groupCourseBaseInfoRequest(schedule.getGroupCourseId()));
        return dto;
    }

    public List<GroupCourseBaseInfoRequest> getAllGroupCoursesWithoutSchedule(){
        List<GroupCourseBaseInfoRequest> allGroupCourses = groupCourseClient.getAllGroupCourses();

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

        List<TeacherBaseInfoRequest> allTeachers = teacherClient.getAllTeachers(Role.TEACHER);
        List<TeacherBaseInfoRequest> availableTeachers = allTeachers.stream().filter(t -> !busyTeachers.contains(t.getId())).toList();

        System.out.println("сообщение будет сгенерировано следующим учителям:");
        availableTeachers.forEach(t -> {
            System.out.println(t.getName() + " " + t.getEmail());
        });
        return availableTeachers.stream().map(TeacherBaseInfoRequest::getId).toList();
    }

    // TODO schedule update logic
    public void update(ScheduleUpdateDto dto){

    }

    public ScheduleBaseDto create(ScheduleCreateDto dto) {
        ClassRoom classRoom = classRoomRepository.findById(dto.getClassRoomId())
                .orElseThrow();

        GroupCourseBaseInfoRequest groupCourse = groupCourseClient
                .groupCourseBaseInfoRequest(dto.getGroupCourseId());
        if (groupCourse.getTeacherId() == null) {
            throw new IllegalArgumentException("Нельзя создать расписание для курса без преподавателя");
        }

        GroupCourseDto groupSize = groupCourseClient
                .getGroupSizeInformation(dto.getGroupCourseId());
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
            TeacherBaseInfoRequest teacherBaseInfoRequest = teacherClient.requestForUserFromUserService(teacherId);
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
