package com.teachsync.courseservice.interaction.feign.responses;

public class GroupCourseResponseForScheduleService {
    private Long groupId;
    private Long courseId;
    private String groupName;
    private String courseName;

    public GroupCourseResponseForScheduleService(Long groupId, Long courseId, String groupName, String courseName) {
        this.groupId = groupId;
        this.courseId = courseId;
        this.groupName = groupName;
        this.courseName = courseName;
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

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
}
