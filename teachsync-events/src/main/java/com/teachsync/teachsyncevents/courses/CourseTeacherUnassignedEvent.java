package com.teachsync.teachsyncevents.courses;

import com.teachsync.teachsyncevents.base.BaseEvent;
import com.teachsync.teachsyncevents.constants.ActionTypes;

public class CourseTeacherUnassignedEvent extends BaseEvent {

    private Long courseId;
    private String courseName;
    private Long previousTeacherId;
    private Long categoryId;
    private String categoryName;
    private String reason;

    public CourseTeacherUnassignedEvent(Long courseId, String courseName, Long previousTeacherId,
                                        Long categoryId, String categoryName, String reason) {
        super("course-service", ActionTypes.COURSE_TEACHER_UNASSIGNED);
        this.courseId = courseId;
        this.courseName = courseName;
        this.previousTeacherId = previousTeacherId;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.reason = reason;
    }

    public CourseTeacherUnassignedEvent() {
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

    public Long getPreviousTeacherId() {
        return previousTeacherId;
    }

    public void setPreviousTeacherId(Long previousTeacherId) {
        this.previousTeacherId = previousTeacherId;
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

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
