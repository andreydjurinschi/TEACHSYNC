package com.teachsync.teachsyncevents.replacements;

import com.teachsync.teachsyncevents.base.BaseEvent;
import com.teachsync.teachsyncevents.constants.ActionTypes;

import java.time.LocalDate;
import java.time.LocalTime;

public class ReplacementApprovedEvent extends BaseEvent {

    private Long replacementRequestId;
    private Long teacherRequestedId;
    private Long approvedTeacherId;
    private String approvedTeacherName;
    private String approvedTeacherEmail;
    private String courseName;
    private String groupName;
    private String classRoomName;
    private LocalDate lessonDate;
    private LocalTime startTime;
    private LocalTime endTime;

    public ReplacementApprovedEvent(Long replacementRequestId, Long teacherRequestedId, Long approvedTeacherId,
                                    String approvedTeacherName, String approvedTeacherEmail,
                                    String courseName, String groupName, String classRoomName,
                                    LocalDate lessonDate, LocalTime startTime, LocalTime endTime) {
        super("replacement-service", ActionTypes.REPLACEMENT_APPROVED);
        this.replacementRequestId = replacementRequestId;
        this.teacherRequestedId = teacherRequestedId;
        this.approvedTeacherId = approvedTeacherId;
        this.approvedTeacherName = approvedTeacherName;
        this.approvedTeacherEmail = approvedTeacherEmail;
        this.courseName = courseName;
        this.groupName = groupName;
        this.classRoomName = classRoomName;
        this.lessonDate = lessonDate;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public ReplacementApprovedEvent() {
    }

    public Long getReplacementRequestId() { return replacementRequestId; }
    public void setReplacementRequestId(Long replacementRequestId) { this.replacementRequestId = replacementRequestId; }
    public Long getTeacherRequestedId() { return teacherRequestedId; }
    public void setTeacherRequestedId(Long teacherRequestedId) { this.teacherRequestedId = teacherRequestedId; }
    public Long getApprovedTeacherId() { return approvedTeacherId; }
    public void setApprovedTeacherId(Long approvedTeacherId) { this.approvedTeacherId = approvedTeacherId; }
    public String getApprovedTeacherName() { return approvedTeacherName; }
    public void setApprovedTeacherName(String approvedTeacherName) { this.approvedTeacherName = approvedTeacherName; }
    public String getApprovedTeacherEmail() { return approvedTeacherEmail; }
    public void setApprovedTeacherEmail(String approvedTeacherEmail) { this.approvedTeacherEmail = approvedTeacherEmail; }
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
}
