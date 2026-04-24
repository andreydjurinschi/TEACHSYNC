package com.teachsync.dto_s.courses;

import com.teachsync.dto_s.groups.GroupShortDto;
import com.teachsync.dto_s.topics.TopicBaseDto;

import java.util.Set;

public class CourseDetailedDto {
    private Long id;
    private String name;
    private String description;
    private Set<TopicBaseDto> topics;
    private Set<GroupShortDto> groups;
    private String categoryName;

    public CourseDetailedDto(Long id, String name, String description, Set<TopicBaseDto> topics, Set<GroupShortDto> groups, String categoryName) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.topics = topics;
        this.groups = groups;
        this.categoryName = categoryName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
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
