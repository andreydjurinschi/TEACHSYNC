package com.teachsync.dto_s.replacementRequest;

import com.teachsync.domain.Status;
import com.teachsync.interaction.requests.ScheduleBaseDtoRequest;
import com.teachsync.interaction.requests.nested.GroupCourseBaseInfoRequest;
import com.teachsync.interaction.requests.nested.TeacherBaseInfoRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ReplacementRequestBaseDto {
    private ScheduleBaseDtoRequest scheduleBaseDtoRequest;
    private TeacherBaseInfoRequest teacherBaseInfoRequest;
    private GroupCourseBaseInfoRequest groupCourseBaseInfoRequest;

    private LocalDateTime requestedAt;
    private LocalDate lessonDate;

    private TeacherBaseInfoRequest approvedByTeacherBaseInfoRequest;
    private String reason;
    private Status status;

    public ReplacementRequestBaseDto(ScheduleBaseDtoRequest scheduleBaseDtoRequest, TeacherBaseInfoRequest teacherBaseInfoRequest, GroupCourseBaseInfoRequest groupCourseBaseInfoRequest, LocalDateTime requestedAt, LocalDate lessonDate, TeacherBaseInfoRequest approvedByTeacherBaseInfoRequest, String reason, Status status) {
        this.scheduleBaseDtoRequest = scheduleBaseDtoRequest;
        this.teacherBaseInfoRequest = teacherBaseInfoRequest;
        this.groupCourseBaseInfoRequest = groupCourseBaseInfoRequest;
        this.requestedAt = requestedAt;
        this.lessonDate = lessonDate;
        this.approvedByTeacherBaseInfoRequest = approvedByTeacherBaseInfoRequest;
        this.reason = reason;
        this.status = status;
    }

    public ScheduleBaseDtoRequest getScheduleBaseDtoRequest() {
        return scheduleBaseDtoRequest;
    }

    public void setScheduleBaseDtoRequest(ScheduleBaseDtoRequest scheduleBaseDtoRequest) {
        this.scheduleBaseDtoRequest = scheduleBaseDtoRequest;
    }

    public TeacherBaseInfoRequest getTeacherBaseInfoRequest() {
        return teacherBaseInfoRequest;
    }

    public void setTeacherBaseInfoRequest(TeacherBaseInfoRequest teacherBaseInfoRequest) {
        this.teacherBaseInfoRequest = teacherBaseInfoRequest;
    }

    public GroupCourseBaseInfoRequest getGroupCourseBaseInfoRequest() {
        return groupCourseBaseInfoRequest;
    }

    public void setGroupCourseBaseInfoRequest(GroupCourseBaseInfoRequest groupCourseBaseInfoRequest) {
        this.groupCourseBaseInfoRequest = groupCourseBaseInfoRequest;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }

    public LocalDate getLessonDate() {
        return lessonDate;
    }

    public void setLessonDate(LocalDate lessonDate) {
        this.lessonDate = lessonDate;
    }

    public TeacherBaseInfoRequest getApprovedByTeacherBaseInfoRequest() {
        return approvedByTeacherBaseInfoRequest;
    }

    public void setApprovedByTeacherBaseInfoRequest(TeacherBaseInfoRequest approvedByTeacherBaseInfoRequest) {
        this.approvedByTeacherBaseInfoRequest = approvedByTeacherBaseInfoRequest;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
