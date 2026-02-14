package org.cedacri.spring.scheduleservice.mappers.schedule;

import org.cedacri.spring.scheduleservice.domain.ClassRoom;
import org.cedacri.spring.scheduleservice.domain.Schedule;
import org.cedacri.spring.scheduleservice.dto_s.domain.class_room.ClassRoomBaseDto;
import org.cedacri.spring.scheduleservice.dto_s.domain.schedule.ScheduleBaseDto;
import org.cedacri.spring.scheduleservice.dto_s.domain.schedule.ScheduleCreateDto;
import org.cedacri.spring.scheduleservice.dto_s.domain.schedule.ScheduleUpdateDto;
import org.cedacri.spring.scheduleservice.mappers.class_room.ClassRoomMapper;

public class ScheduleMapper {

    // TODO: group course feign finder
    // null on teacher field is replaced in schedule service

    public static ScheduleBaseDto mapToBaseDto(Schedule schedule) {
        ClassRoomBaseDto classRoomBaseDto = ClassRoomMapper.mapToBaseDto(schedule.getClassRoom());
        return new ScheduleBaseDto(
                schedule.getId(), schedule.getStartTime(), schedule.getEndTime(), schedule.getWeekDays(), null, null, classRoomBaseDto
        );
    }


    // TODO: groupCourseId, teacherId (from feign request), classRoomId
    public static Schedule mapScheduleUpdateDtoToEntity(ScheduleUpdateDto dto){
        return new Schedule(
                dto.getStartTime(), dto.getEndTime(), dto.getWeekDays(), null, null, null
        );
    }

    // TODO: groupCourseId, teacherId (from feign request), classRoomId
    public static Schedule mapScheduleCreateDtoToEntity(ScheduleCreateDto dto) {
        return new Schedule(
                dto.getStartTime(), dto.getEndTime(), dto.getWeekDays(), null, null, null
        );
    }
}
