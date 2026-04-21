package com.teachsync.interation.feign.requests;

public class GroupCourseBaseInfoRequest {
    private Long id;
    private Long groupId;
    private Long courseId;
    private String groupName;
    private String courseName;
    private Long teacherId;
    private String teacherName;

    public GroupCourseBaseInfoRequest(Long id, Long groupId, Long courseId, String groupName, String courseName, Long teacherId, String teacherName) {
        this.id = id;
        this.groupId = groupId;
        this.courseId = courseId;
        this.groupName = groupName;
        this.courseName = courseName;
        this.teacherId = teacherId;
        this.teacherName = teacherName;
    }

    public GroupCourseBaseInfoRequest() {
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

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }
}
