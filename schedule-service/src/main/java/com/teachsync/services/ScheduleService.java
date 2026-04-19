package com.teachsync.services;

import com.teachsync.domain.ClassRoom;
import com.teachsync.domain.Schedule;
import com.teachsync.domain.WeekDays;
import com.teachsync.dto_s.domain.class_room.ClassRoomBaseDto;
import com.teachsync.dto_s.domain.schedule.ScheduleBaseDto;
import com.teachsync.dto_s.domain.schedule.ScheduleCreateDto;
import com.teachsync.dto_s.domain.schedule.ScheduleUpdateDto;
import com.teachsync.exceptions.InvalidTimeRangeException;
import com.teachsync.interation.feign.Role;
import com.teachsync.interation.feign.clients.GroupCourseClient;
import com.teachsync.interation.feign.clients.TeacherClient;
import com.teachsync.interation.feign.requests.GroupCourseBaseInfoRequest;
import com.teachsync.interation.feign.requests.TeacherBaseInfoRequest;
import com.teachsync.mappers.schedule.ScheduleMapper;
import com.teachsync.repositories.ClassRoomRepository;
import com.teachsync.repositories.ScheduleRepository;
import com.teachsync.validator.CustomTimeValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ClassRoomRepository classRoomRepository;
    private final CustomTimeValidator timeValidator;
    private final TeacherClient teacherClient;
    private final GroupCourseClient groupCourseClient;

    public ScheduleService(ScheduleRepository scheduleRepository, ClassRoomRepository classRoomRepository, CustomTimeValidator timeValidator, TeacherClient teacherClient, GroupCourseClient groupCourseClient) {
        this.scheduleRepository = scheduleRepository;
        this.classRoomRepository = classRoomRepository;
        this.timeValidator = timeValidator;
        this.teacherClient = teacherClient;
        this.groupCourseClient = groupCourseClient;
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
        return ScheduleMapper.mapToBaseDto(schedule);
    }

    public List<Long> findAvailableTeachers(Long scheduleId, WeekDays weekDays) {

        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow();

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

    @Transactional
    public void create(ScheduleCreateDto dto) throws InvalidTimeRangeException {
        timeValidator.checkTime(dto.getStartTime(), dto.getEndTime());
        ClassRoom classRoom = classRoomRepository.findById(dto.getClassRoomId()).orElseThrow(() -> new NoSuchElementException("this classroom does not exist"));
        TeacherBaseInfoRequest teacherBaseInfoRequest = teacherClient.requestForUserFromUserService(dto.getTeacherId());
        if(teacherBaseInfoRequest == null){
            throw new IllegalArgumentException("unknown teacher");
        }
        GroupCourseBaseInfoRequest groupCourseBaseInfoRequest = groupCourseClient.groupCourseBaseInfoRequest(dto.getGroupCourseId());
        if(groupCourseBaseInfoRequest == null){
            throw new IllegalArgumentException("unknown group course relation");
        }

        Schedule schedule = ScheduleMapper.mapScheduleCreateDtoToEntity(
                dto
        );
        schedule.setClassRoom(classRoom);
        scheduleRepository.save(schedule);
    }

    //TODO: schedule delete logic
    public void delete(Long id){

    }
}
