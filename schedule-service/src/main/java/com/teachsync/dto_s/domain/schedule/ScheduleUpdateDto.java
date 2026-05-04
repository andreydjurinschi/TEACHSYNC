package com.teachsync.dto_s.domain.schedule;

import com.teachsync.domain.WeekDays;

import java.time.LocalTime;
import java.util.Set;

public class ScheduleUpdateDto {
    private LocalTime startTime;
    private LocalTime endTime;
    private Set<WeekDays> weekDays;
    private Long groupCourseId;
    private Long classRoomId;

    public ScheduleUpdateDto() {
    }

    public ScheduleUpdateDto(LocalTime startTime, LocalTime endTime, Set<WeekDays> weekDays, Long groupCourseId, Long classRoomId) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.weekDays = weekDays;
        this.groupCourseId = groupCourseId;
        this.classRoomId = classRoomId;
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

    public Set<WeekDays> getWeekDays() {
        return weekDays;
    }

    public void setWeekDays(Set<WeekDays> weekDays) {
        this.weekDays = weekDays;
    }

    public Long getGroupCourseId() {
        return groupCourseId;
    }

    public void setGroupCourseId(Long groupCourseId) {
        this.groupCourseId = groupCourseId;
    }

    public Long getClassRoomId() {
        return classRoomId;
    }

    public void setClassRoomId(Long classRoomId) {
        this.classRoomId = classRoomId;
    }
}
