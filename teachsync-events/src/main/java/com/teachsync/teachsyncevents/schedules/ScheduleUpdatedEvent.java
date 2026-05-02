package com.teachsync.teachsyncevents.schedules;

import com.teachsync.teachsyncevents.base.BaseEvent;
import com.teachsync.teachsyncevents.constants.ActionTypes;

import java.time.LocalTime;
import java.util.Set;

public class ScheduleUpdatedEvent extends BaseEvent {

    private Long scheduleId;
    private Long previousTeacherId;
    private Long teacherId;
    private Long groupCourseId;
    private String courseName;
    private String groupName;
    private String classRoomName;
    private LocalTime startTime;
    private LocalTime endTime;
    private Set<String> weekDays;
    private Long changedByUserId;
    private String changedByName;
    private String changeSummary;

    public ScheduleUpdatedEvent() {
    }

    public ScheduleUpdatedEvent(Long scheduleId,
                                Long previousTeacherId,
                                Long teacherId,
                                Long groupCourseId,
                                String courseName,
                                String groupName,
                                String classRoomName,
                                LocalTime startTime,
                                LocalTime endTime,
                                Set<String> weekDays,
                                Long changedByUserId,
                                String changedByName,
                                String changeSummary) {
        super("schedule-service", ActionTypes.SCHEDULE_UPDATED);
        this.scheduleId = scheduleId;
        this.previousTeacherId = previousTeacherId;
        this.teacherId = teacherId;
        this.groupCourseId = groupCourseId;
        this.courseName = courseName;
        this.groupName = groupName;
        this.classRoomName = classRoomName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.weekDays = weekDays;
        this.changedByUserId = changedByUserId;
        this.changedByName = changedByName;
        this.changeSummary = changeSummary;
    }

    public Long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public Long getPreviousTeacherId() {
        return previousTeacherId;
    }

    public void setPreviousTeacherId(Long previousTeacherId) {
        this.previousTeacherId = previousTeacherId;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public Long getGroupCourseId() {
        return groupCourseId;
    }

    public void setGroupCourseId(Long groupCourseId) {
        this.groupCourseId = groupCourseId;
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

    public String getClassRoomName() {
        return classRoomName;
    }

    public void setClassRoomName(String classRoomName) {
        this.classRoomName = classRoomName;
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

    public Set<String> getWeekDays() {
        return weekDays;
    }

    public void setWeekDays(Set<String> weekDays) {
        this.weekDays = weekDays;
    }

    public Long getChangedByUserId() {
        return changedByUserId;
    }

    public void setChangedByUserId(Long changedByUserId) {
        this.changedByUserId = changedByUserId;
    }

    public String getChangedByName() {
        return changedByName;
    }

    public void setChangedByName(String changedByName) {
        this.changedByName = changedByName;
    }

    public String getChangeSummary() {
        return changeSummary;
    }

    public void setChangeSummary(String changeSummary) {
        this.changeSummary = changeSummary;
    }
}
