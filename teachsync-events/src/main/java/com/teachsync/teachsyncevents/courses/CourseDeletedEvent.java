package com.teachsync.teachsyncevents.courses;

import com.teachsync.teachsyncevents.base.BaseEvent;
import com.teachsync.teachsyncevents.constants.ActionTypes;

public class CourseDeletedEvent extends BaseEvent {

    private Long courseId;
    private String courseName;
    private Long teacherId;
    private String categoryName;
    private Long changedByUserId;
    private String changedByName;

    public CourseDeletedEvent() {
    }

    public CourseDeletedEvent(Long courseId,
                              String courseName,
                              Long teacherId,
                              String categoryName,
                              Long changedByUserId,
                              String changedByName) {
        super("course-service", ActionTypes.COURSE_DELETED);
        this.courseId = courseId;
        this.courseName = courseName;
        this.teacherId = teacherId;
        this.categoryName = categoryName;
        this.changedByUserId = changedByUserId;
        this.changedByName = changedByName;
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

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Long getChangedByUserId() {
        return changedByUserId;
    }

    public void setChangedByUserId(Long changedByUserId) {
        this.changedByUserId = changedByUserId;
    }

    public String getChangedByName() {
        return changedByName;
    }

    public void setChangedByName(String changedByName) {
        this.changedByName = changedByName;
    }
}
