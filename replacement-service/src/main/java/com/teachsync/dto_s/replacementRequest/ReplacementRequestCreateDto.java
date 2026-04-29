package com.teachsync.dto_s.replacementRequest;

import java.time.LocalDate;

public class ReplacementRequestCreateDto {
    private Long scheduleId;
    private Long teacherRequested;
    private LocalDate lessonDate;
    private String reason;

    public Long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public Long getTeacherRequested() {
        return teacherRequested;
    }

    public void setTeacherRequested(Long teacherRequested) {
        this.teacherRequested = teacherRequested;
    }

    public LocalDate getLessonDate() {
        return lessonDate;
    }

    public void setLessonDate(LocalDate lessonDate) {
        this.lessonDate = lessonDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
