package com.teachsync.teachsyncevents.courses;

import com.teachsync.teachsyncevents.base.BaseEvent;
import com.teachsync.teachsyncevents.constants.ActionTypes;

public class CourseTopicRemovedEvent extends BaseEvent {
    private Long courseId;
    private Long topicId;
    private String courseName;
    private String topicName;
    private Long teacherId;

    public CourseTopicRemovedEvent() {
    }

    public CourseTopicRemovedEvent(Long courseId, Long topicId, String courseName, String topicName, Long teacherId) {
        super("course-service", ActionTypes.COURSE_TOPIC_REMOVED);
        this.courseId = courseId;
        this.topicId = topicId;
        this.courseName = courseName;
        this.topicName = topicName;
        this.teacherId = teacherId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }
}
