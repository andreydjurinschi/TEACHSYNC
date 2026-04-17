package com.teachsync.dto_s.courses;

import com.teachsync.dto_s.groups.GroupBaseDto;

import java.util.List;

public class CourseWithGroupDto {
    private Long id;
    private String name;
    private String  description;
    private String photoUrl;
    private Long teacherId;
    private Long categoryId;
    private String categoryName;
    private List<GroupBaseDto> groups;

    public CourseWithGroupDto() {
    }

    public CourseWithGroupDto(Long id, String name, String description, String photoUrl, Long teacherId, Long categoryId, String categoryName, List<GroupBaseDto> groups) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.photoUrl = photoUrl;
        this.teacherId = teacherId;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.groups = groups;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<GroupBaseDto> getGroups() {
        return groups;
    }

    public void setGroups(List<GroupBaseDto> groups) {
        this.groups = groups;
    }
}
