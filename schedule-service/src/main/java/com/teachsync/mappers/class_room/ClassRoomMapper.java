package com.teachsync.mappers.class_room;

import com.teachsync.domain.ClassRoom;
import com.teachsync.dto_s.domain.class_room.ClassRoomBaseDto;
import com.teachsync.dto_s.domain.class_room.ClassRoomCreateUpdateDto;
import com.teachsync.dto_s.domain.class_room.ClassRoomDetailedDto;
import com.teachsync.dto_s.domain.schedule.ScheduleBaseDto;
import com.teachsync.mappers.schedule.ScheduleMapper;

import java.util.Set;
import java.util.stream.Collectors;

public class ClassRoomMapper {
    public static ClassRoomBaseDto mapToBaseDto(ClassRoom classRoom){
        return new ClassRoomBaseDto(classRoom.getId(), classRoom.getName(), classRoom.getCapacity());
    }

    public static ClassRoomDetailedDto mapToDetailedDto(ClassRoom classRoom){
        Set<ScheduleBaseDto> scheduleBaseDtos = classRoom.getSchedules().stream().map(ScheduleMapper::mapToBaseDto).collect(Collectors.toSet());
        return new ClassRoomDetailedDto(
                classRoom.getId(), classRoom.getName(), classRoom.getCapacity(), classRoom.getPhotoUrl(), scheduleBaseDtos
        );
    }

    public static ClassRoom mapToEntity(ClassRoomCreateUpdateDto classRoomCreateUpdateDto){
        return new ClassRoom(
                classRoomCreateUpdateDto.getName(), classRoomCreateUpdateDto.getCapacity(), classRoomCreateUpdateDto.getPhotoUrl()
        );
    }
}
