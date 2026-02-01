package com.teachsync.courseservice.mappers;

import com.teachsync.courseservice.domain.Course;
import com.teachsync.courseservice.dto_s.courses.CourseBaseDto;
import com.teachsync.courseservice.dto_s.courses.CourseCreateDto;
import com.teachsync.courseservice.dto_s.courses.CourseDetailedDto;
import com.teachsync.courseservice.dto_s.courses.CourseShortDto;
import com.teachsync.courseservice.dto_s.groups.GroupShortDto;
import com.teachsync.courseservice.dto_s.topics.TopicBaseDto;

import java.util.Set;
import java.util.stream.Collectors;

public class CourseMapper {

    public static CourseBaseDto mapToBaseDto(Course course) {
        return new CourseBaseDto(
                course.getId(), course.getName(), course.getDescription(), course.getPhotoUrl(), course.getTeacherId()
        );
    }

    //TODO: null till teacher verification logic is not created
    public static Course mapToEntity(CourseCreateDto dto) {
        return new Course(
                dto.getName(), dto.getDescription(), dto.getPhotoUrl(), null
        );
    }

    public static CourseDetailedDto mapToDetailedDto(Course course){
        Set<TopicBaseDto> topicBaseDtoSet = course.getTopics().stream().map(topic -> new TopicBaseDto(topic.getName())).collect(Collectors.toSet());
        Set<GroupShortDto> groupShortDtoSet = course.getGroups().stream().map(group -> new GroupShortDto(group.getName())).collect(Collectors.toSet());
        return new CourseDetailedDto(
                course.getName(), course.getDescription(), topicBaseDtoSet, groupShortDtoSet
        );
    }

    public static CourseShortDto mapToShortDto(Course course){
        return new CourseShortDto(course.getId(), course.getName());
    }
}
