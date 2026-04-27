package com.teachsync.teachsyncevents.courses;

import com.teachsync.teachsyncevents.base.BaseEvent;
import com.teachsync.teachsyncevents.constants.ActionTypes;

public class CourseGroupEnrolledEvent extends BaseEvent {
    private Long courseId;
    private Long groupId;
    private String courseName;
    private String groupName;
    private Long teacherId;

    public CourseGroupEnrolledEvent(Long courseId, Long groupId, String courseName, String groupName, Long teacherId) {
        super("course-service", ActionTypes.COURSE_GROUP_ENROLLED);
        this.courseId = courseId;
        this.groupId = groupId;
        this.courseName = courseName;
        this.groupName = groupName;
        this.teacherId = teacherId;
    }

    public CourseGroupEnrolledEvent() {
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
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
