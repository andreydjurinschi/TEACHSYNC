package com.teachsync.domain;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "replacements")
public class ReplacementRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long scheduleId;
    private Long groupCourseId;
    private LocalDateTime requestedAt;
    private LocalDate lessonDate;
    private Long approvedById;
    private String reason;
    private Long teacherRequested;
    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToMany(mappedBy = "replacementRequest")
    private Set<ReplacementResponse> replacementResponses = new HashSet<>();

    public ReplacementRequest(Long scheduleId, Long groupCourseId, LocalDateTime requestedAt, LocalDate lessonDate, Long approvedById, String reason, Long teacherRequested, Status status) {
        this.scheduleId = scheduleId;
        this.groupCourseId = groupCourseId;
        this.requestedAt = requestedAt;
        this.lessonDate = lessonDate;
        this.approvedById = approvedById;
        this.reason = reason;
        this.teacherRequested = teacherRequested;
        this.status = status;
    }

    public LocalDate getLessonDate() {
        return lessonDate;
    }

    public void setLessonDate(LocalDate lessonDate) {
        this.lessonDate = lessonDate;
    }

    public ReplacementRequest() {
    }

    public Long getTeacherRequested() {
        return teacherRequested;
    }

    public void setTeacherRequested(Long teacherRequested) {
        this.teacherRequested = teacherRequested;
    }

    public Long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public Long getGroupCourseId() {
        return groupCourseId;
    }

    public void setGroupCourseId(Long courseTopicId) {
        this.groupCourseId = courseTopicId;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }

    public Long getApprovedById() {
        return approvedById;
    }

    public void setApprovedById(Long approvedById) {
        this.approvedById = approvedById;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Set<ReplacementResponse> getReplacementResponses() {
        return replacementResponses;
    }

    public void setReplacementResponses(Set<ReplacementResponse> replacementResponses) {
        this.replacementResponses = replacementResponses;
    }

}
