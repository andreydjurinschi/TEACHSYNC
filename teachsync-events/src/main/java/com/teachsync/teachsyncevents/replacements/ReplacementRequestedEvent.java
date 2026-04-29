package com.teachsync.teachsyncevents.replacements;

import com.teachsync.teachsyncevents.base.BaseEvent;
import com.teachsync.teachsyncevents.constants.ActionTypes;

import java.time.LocalDate;
import java.time.LocalTime;

public class ReplacementRequestedEvent extends BaseEvent {

    private Long replacementRequestId;
    private Long teacherRequestedId;
    private Long candidateTeacherId;
    private Long scheduleId;
    private String courseName;
    private String groupName;
    private String classRoomName;
    private LocalDate lessonDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String reason;

    public ReplacementRequestedEvent(Long replacementRequestId, Long teacherRequestedId, Long candidateTeacherId,
                                     Long scheduleId, String courseName, String groupName, String classRoomName,
                                     LocalDate lessonDate, LocalTime startTime, LocalTime endTime, String reason) {
        super("replacement-service", ActionTypes.REPLACEMENT_REQUESTED);
        this.replacementRequestId = replacementRequestId;
        this.teacherRequestedId = teacherRequestedId;
        this.candidateTeacherId = candidateTeacherId;
        this.scheduleId = scheduleId;
        this.courseName = courseName;
        this.groupName = groupName;
        this.classRoomName = classRoomName;
        this.lessonDate = lessonDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.reason = reason;
    }

    public ReplacementRequestedEvent() {
    }

    public Long getReplacementRequestId() { return replacementRequestId; }
    public void setReplacementRequestId(Long replacementRequestId) { this.replacementRequestId = replacementRequestId; }
    public Long getTeacherRequestedId() { return teacherRequestedId; }
    public void setTeacherRequestedId(Long teacherRequestedId) { this.teacherRequestedId = teacherRequestedId; }
    public Long getCandidateTeacherId() { return candidateTeacherId; }
    public void setCandidateTeacherId(Long candidateTeacherId) { this.candidateTeacherId = candidateTeacherId; }
    public Long getScheduleId() { return scheduleId; }
    public void setScheduleId(Long scheduleId) { this.scheduleId = scheduleId; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }
    public String getClassRoomName() { return classRoomName; }
    public void setClassRoomName(String classRoomName) { this.classRoomName = classRoomName; }
    public LocalDate getLessonDate() { return lessonDate; }
    public void setLessonDate(LocalDate lessonDate) { this.lessonDate = lessonDate; }
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
