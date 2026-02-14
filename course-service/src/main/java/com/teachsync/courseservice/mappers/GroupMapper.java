package com.teachsync.courseservice.mappers;

import com.teachsync.courseservice.domain.Group;
import com.teachsync.courseservice.dto_s.groups.GroupBaseDto;
import com.teachsync.courseservice.dto_s.groups.GroupCreateDto;
import com.teachsync.courseservice.dto_s.groups.GroupWithCoursesDto;

public class GroupMapper {

    public static GroupBaseDto mapToBaseDto(Group group){
        return new GroupBaseDto(
                group.getName(), group.getDate(), group.getCapacity()
        );
    }

    // null on course set field is replaced in group service
    public static GroupWithCoursesDto mapToDetailedDto(Group group) {
        return new GroupWithCoursesDto(
                group.getName(), group.getDate(), group.getCapacity(), null
        );
    }

    public static Group mapToEntity(GroupCreateDto dto) {
        return new Group(
                dto.getName(), dto.getOpenDate(), dto.getCapacity()
        );
    }
}
