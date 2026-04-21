package com.teachsync.services.feign.groupcourse;

public class GroupCourseSizeDto {
    private Long id;
    private Long groupId;
    private Long courseId;
    private int capacity;

    public GroupCourseSizeDto(Long id, Long groupId, Long courseId, int capacity) {
        this.id = id;
        this.groupId = groupId;
        this.courseId = courseId;
        this.capacity = capacity;
    }

    public GroupCourseSizeDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
