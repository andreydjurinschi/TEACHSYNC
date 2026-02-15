package com.teachsync.services;

import com.teachsync.domain.Schedule;
import com.teachsync.dto_s.domain.schedule.ScheduleBaseDto;
import com.teachsync.dto_s.domain.schedule.ScheduleCreateDto;
import com.teachsync.dto_s.domain.schedule.ScheduleUpdateDto;
import com.teachsync.exceptions.InvalidTimeRangeException;
import com.teachsync.interation.feign.clients.GroupCourseClient;
import com.teachsync.interation.feign.clients.TeacherClient;
import com.teachsync.interation.feign.requests.GroupCourseBaseInfoRequest;
import com.teachsync.interation.feign.requests.TeacherBaseInfoRequest;
import com.teachsync.mappers.schedule.ScheduleMapper;
import com.teachsync.repositories.ScheduleRepository;
import com.teachsync.validator.CustomTimeValidator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final CustomTimeValidator timeValidator;
    private final TeacherClient teacherClient;
    private final GroupCourseClient groupCourseClient;

    public ScheduleService(ScheduleRepository scheduleRepository, CustomTimeValidator timeValidator, TeacherClient teacherClient, GroupCourseClient groupCourseClient) {
        this.scheduleRepository = scheduleRepository;
        this.timeValidator = timeValidator;
        this.teacherClient = teacherClient;
        this.groupCourseClient = groupCourseClient;
    }

    public List<ScheduleBaseDto> getAll(){
        List<Schedule> all = scheduleRepository.findWithClassRooms();
        List<ScheduleBaseDto> baseDtos = new ArrayList<>();
        for(var el : all){
            TeacherBaseInfoRequest teacherBaseInfoRequest = teacherClient.requestForUserFromUserService(el.getTeacherId());
            GroupCourseBaseInfoRequest groupCourseBaseInfoRequest = groupCourseClient.groupCourseBaseInfoRequest(el.getGroupCourseId());
            ScheduleBaseDto baseDto = ScheduleMapper.mapToBaseDto(el);
            baseDto.setTeacherDto(teacherBaseInfoRequest);
            baseDto.setGroupCourseDto(groupCourseBaseInfoRequest);
            baseDtos.add(baseDto);
        }
        return baseDtos;
    }

    public ScheduleBaseDto getById(Long id){
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("this schedule does not exist"));
        return ScheduleMapper.mapToBaseDto(schedule);
    }

    // TODO schedule update logic
    public void update(ScheduleUpdateDto dto){

    }

    public void create(ScheduleCreateDto dto) throws InvalidTimeRangeException {
        timeValidator.checkTime(dto.getStartTime(), dto.getEndTime());
        Schedule schedule = ScheduleMapper.mapScheduleCreateDtoToEntity(dto);
        scheduleRepository.save(schedule);
    }

    //TODO: schedule delete logic
    public void delete(Long id){

    }
}
