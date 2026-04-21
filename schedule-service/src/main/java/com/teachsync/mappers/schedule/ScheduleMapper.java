package com.teachsync.mappers.schedule;

import com.teachsync.domain.Schedule;
import com.teachsync.dto_s.domain.class_room.ClassRoomBaseDto;
import com.teachsync.dto_s.domain.schedule.ScheduleBaseDto;
import com.teachsync.dto_s.domain.schedule.ScheduleCreateDto;
import com.teachsync.dto_s.domain.schedule.ScheduleUpdateDto;
import com.teachsync.mappers.class_room.ClassRoomMapper;

import java.util.Set;
import java.util.stream.Collectors;

public class ScheduleMapper {

    public static ScheduleBaseDto mapToBaseDto(Schedule schedule) {
        ClassRoomBaseDto classRoomBaseDto = ClassRoomMapper.mapToBaseDto(schedule.getClassRoom());

        Set<String> days = schedule.getWeekDays()
                .stream()
                .map(sd -> sd.getWeekday().name())  // ← достаём enum как строку "MON", "TUE"
                .collect(Collectors.toSet());

        return new ScheduleBaseDto(
                schedule.getId(),
                schedule.getStartTime(),
                schedule.getEndTime(),
                days,
                null,
                null,
                classRoomBaseDto
        );
    }

    // TODO: groupCourseId, teacherId (from feign request), classRoomId
/*    public static Schedule mapScheduleUpdateDtoToEntity(ScheduleUpdateDto dto){
        return new Schedule(
                dto.getStartTime(), dto.getEndTime(), null, null, null
        );
    }*/

    // TODO: groupCourseId, teacherId (from feign request), classRoomId
    public static Schedule mapScheduleCreateDtoToEntity(ScheduleCreateDto dto) {
        return new Schedule(
                dto.getStartTime(), dto.getEndTime(), dto.getWeekDays() ,dto.getGroupCourseId(), dto.getTeacherId(), null
        );
    }
}
