package com.teachsync.teachsyncevents.replacements;

import com.teachsync.teachsyncevents.base.BaseEvent;
import com.teachsync.teachsyncevents.constants.ActionTypes;

import java.time.LocalDate;
import java.time.LocalTime;

public class ReplacementStatusChangedEvent extends BaseEvent {

    private Long replacementRequestId;
    private Long teacherRequestedId;
    private String status;
    private String courseName;
    private String groupName;
    private LocalDate lessonDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String message;

    public ReplacementStatusChangedEvent(Long replacementRequestId, Long teacherRequestedId, String status,
                                         String courseName, String groupName,
                                         LocalDate lessonDate, LocalTime startTime, LocalTime endTime,
                                         String message) {
        super("replacement-service", ActionTypes.REPLACEMENT_STATUS_CHANGED);
        this.replacementRequestId = replacementRequestId;
        this.teacherRequestedId = teacherRequestedId;
        this.status = status;
        this.courseName = courseName;
        this.groupName = groupName;
        this.lessonDate = lessonDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.message = message;
    }

    public ReplacementStatusChangedEvent() {
    }

    public Long getReplacementRequestId() {
        return replacementRequestId;
    }

    public void setReplacementRequestId(Long replacementRequestId) {
        this.replacementRequestId = replacementRequestId;
    }

    public Long getTeacherRequestedId() {
        return teacherRequestedId;
    }

    public void setTeacherRequestedId(Long teacherRequestedId) {
        this.teacherRequestedId = teacherRequestedId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public LocalDate getLessonDate() {
        return lessonDate;
    }

    public void setLessonDate(LocalDate lessonDate) {
        this.lessonDate = lessonDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
