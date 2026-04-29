package com.teachsync.teachsyncevents.courses;

import com.teachsync.teachsyncevents.base.BaseEvent;
import com.teachsync.teachsyncevents.constants.ActionTypes;

public class CourseTeacherAssignmentRequestedEvent extends BaseEvent {

    private Long courseId;
    private String courseName;
    private Long teacherId;
    private Long categoryId;
    private String categoryName;

    public CourseTeacherAssignmentRequestedEvent(Long courseId, String courseName, Long teacherId,
                                                 Long categoryId, String categoryName) {
        super("course-service", ActionTypes.COURSE_TEACHER_ASSIGNMENT_REQUESTED);
        this.courseId = courseId;
        this.courseName = courseName;
        this.teacherId = teacherId;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    public CourseTeacherAssignmentRequestedEvent() {
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
}
