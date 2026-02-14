package com.teachsync.courseservice.dto_s.courses;

import com.teachsync.courseservice.dto_s.groups.GroupShortDto;
import com.teachsync.courseservice.dto_s.topics.TopicBaseDto;

import java.util.Set;

public class CourseDetailedDto {

    private String name;
    private String description;
    private Set<TopicBaseDto> topics;
    private Set<GroupShortDto> groups;

    // TODO: teacher base dto from feign request
    /*private TeacherBaseDto teacherDto;*/
    public CourseDetailedDto(String name, String description, Set<TopicBaseDto> topics, Set<GroupShortDto> groups) {
        this.name = name;
        this.description = description;
        this.topics = topics;
        this.groups = groups;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<TopicBaseDto> getTopics() {
        return topics;
    }

    public void setTopics(Set<TopicBaseDto> topics) {
        this.topics = topics;
    }

    public Set<GroupShortDto> getGroups() {
        return groups;
    }

    public void setGroups(Set<GroupShortDto> groups) {
        this.groups = groups;
    }
}
