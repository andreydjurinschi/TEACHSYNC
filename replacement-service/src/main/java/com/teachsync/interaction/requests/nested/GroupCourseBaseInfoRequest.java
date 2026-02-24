package com.teachsync.interaction.requests.nested;

public class GroupCourseBaseInfoRequest {
    private Long groupId;
    private Long courseId;
    private String groupName;
    private String courseName;

    public GroupCourseBaseInfoRequest(Long groupId, Long courseId, String courseName, String groupName) {
        this.groupId = groupId;
        this.courseId = courseId;
        this.courseName = courseName;
        this.groupName = groupName;
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

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}


