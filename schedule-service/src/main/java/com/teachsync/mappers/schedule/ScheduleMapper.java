package com.teachsync.mappers.schedule;

import com.teachsync.domain.Schedule;
import com.teachsync.dto_s.domain.class_room.ClassRoomBaseDto;
import com.teachsync.dto_s.domain.schedule.ScheduleBaseDto;
import com.teachsync.dto_s.domain.schedule.ScheduleCreateDto;
import com.teachsync.dto_s.domain.schedule.ScheduleUpdateDto;
import com.teachsync.mappers.class_room.ClassRoomMapper;

public class ScheduleMapper {

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
                dto.getStartTime(), dto.getEndTime(), dto.getWeekDays(), dto.getGroupCourseId(), dto.getTeacherId(), null
        );
    }
}
