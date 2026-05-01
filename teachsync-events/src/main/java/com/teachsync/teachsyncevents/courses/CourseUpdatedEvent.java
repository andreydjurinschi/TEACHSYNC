package com.teachsync.teachsyncevents.courses;

import com.teachsync.teachsyncevents.base.BaseEvent;
import com.teachsync.teachsyncevents.constants.ActionTypes;

public class CourseUpdatedEvent extends BaseEvent {

    private Long courseId;
    private String courseName;
    private String oldState;
    private String newState;

    public CourseUpdatedEvent() {
    }

    public CourseUpdatedEvent(Long courseId, String courseName, String oldState, String newState) {
        super("course-service", ActionTypes.COURSE_EDITED);
        this.courseId = courseId;
        this.courseName = courseName;
        this.oldState = oldState;
        this.newState = newState;
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

    public String getOldState() {
        return oldState;
    }

    public void setOldState(String oldState) {
        this.oldState = oldState;
    }

    public String getNewState() {
        return newState;
    }

    public void setNewState(String newState) {
        this.newState = newState;
    }

    @Override
    public String toString() {
        return "courseId=" + courseId +
                ", courseName='" + courseName + '\'' +
                ", oldState='" + oldState + '\'' +
                ", newState='" + newState + '\'' +
                '}';
    }
}
