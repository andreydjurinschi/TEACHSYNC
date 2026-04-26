package com.teachsync.teachsyncevents.courses;

import com.teachsync.teachsyncevents.base.BaseEvent;
import com.teachsync.teachsyncevents.constants.ActionTypes;

public class CourseTeacherAssignedEvent extends BaseEvent {

    private Long courseId;
    private String courseName;
    private Long teacherAssigned;

    public CourseTeacherAssignedEvent(Long courseId, String courseName, Long teacherAssigned) {
        super("course-service", ActionTypes.COURSE_TEACHER_ASSIGNED);
        this.courseId = courseId;
        this.courseName = courseName;
        this.teacherAssigned = teacherAssigned;
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

    public Long getTeacherAssigned() {
        return teacherAssigned;
    }

    public void setTeacherAssigned(Long teacherAssigned) {
        this.teacherAssigned = teacherAssigned;
    }
}
