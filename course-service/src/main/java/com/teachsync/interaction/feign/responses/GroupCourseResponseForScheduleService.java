package com.teachsync.interaction.feign.responses;

public class GroupCourseResponseForScheduleService {
    private Long id;
    private Long groupId;
    private Long courseId;
    private String groupName;
    private String courseName;
    private Long categoryId;
    private String categoryName;
    private Long teacherId;
    private String teacherName;

    public GroupCourseResponseForScheduleService(Long id, Long groupId, Long courseId, String groupName, String courseName, Long teacherId, String teacherName) {
        this(id, groupId, courseId, groupName, courseName, null, null, teacherId, teacherName);
    }

    public GroupCourseResponseForScheduleService(Long id, Long groupId, Long courseId, String groupName, String courseName,
                                                 Long categoryId, String categoryName, Long teacherId, String teacherName) {
        this.id = id;
        this.groupId = groupId;
        this.courseId = courseId;
        this.groupName = groupName;
        this.courseName = courseName;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.teacherId = teacherId;
        this.teacherName = teacherName;
    }

    public GroupCourseResponseForScheduleService() {
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
