package org.cedacri.spring.scheduleservice.dto_s.domain.schedule;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.cedacri.spring.scheduleservice.domain.WeekDays;

import java.time.LocalTime;
import java.util.Set;

public class ScheduleCreateDto {
    @NotNull(message = "start time for schedule is required")
    private LocalTime startTime;
    @NotNull(message = "end time for schedule is required")
    private LocalTime endTime;
    @NotEmpty(message = "at least one week day is required")
    private Set<WeekDays> weekDays;
    @NotNull
    private Long groupCourseId;
    private Long teacherId;
    @NotNull
    private Long classRoomId;

    public ScheduleCreateDto(LocalTime startTime, LocalTime endTime, Set<WeekDays> weekDays, Long groupCourseId, Long teacherId, Long classRoomId) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.weekDays = weekDays;
        this.groupCourseId = groupCourseId;
        this.teacherId = teacherId;
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

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public Long getClassRoomId() {
        return classRoomId;
    }

    public void setClassRoomId(Long classRoomId) {
        this.classRoomId = classRoomId;
    }
}
