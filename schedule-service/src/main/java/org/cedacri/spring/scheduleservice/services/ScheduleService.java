package org.cedacri.spring.scheduleservice.services;

import org.cedacri.spring.scheduleservice.domain.Schedule;
import org.cedacri.spring.scheduleservice.dto_s.domain.schedule.ScheduleBaseDto;
import org.cedacri.spring.scheduleservice.dto_s.domain.schedule.ScheduleCreateDto;
import org.cedacri.spring.scheduleservice.dto_s.domain.schedule.ScheduleUpdateDto;
import org.cedacri.spring.scheduleservice.exceptions.InvalidTimeRangeException;
import org.cedacri.spring.scheduleservice.interation.feign.clients.GroupCourseClient;
import org.cedacri.spring.scheduleservice.interation.feign.clients.TeacherClient;
import org.cedacri.spring.scheduleservice.interation.feign.requests.GroupCourseBaseInfoRequest;
import org.cedacri.spring.scheduleservice.interation.feign.requests.TeacherBaseInfoRequest;
import org.cedacri.spring.scheduleservice.mappers.schedule.ScheduleMapper;
import org.cedacri.spring.scheduleservice.repositories.ScheduleRepository;
import org.cedacri.spring.scheduleservice.validator.CustomTimeValidator;
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
