package org.cedacri.spring.scheduleservice.dto_s.domain.schedule;

import org.cedacri.spring.scheduleservice.domain.WeekDays;
import org.cedacri.spring.scheduleservice.dto_s.feign.GroupCourseDto;
import org.cedacri.spring.scheduleservice.dto_s.feign.TeacherDto;

import java.time.LocalTime;
import java.util.Set;

public class ScheduleUpdateDto {
    private LocalTime startTime;
    private LocalTime endTime;
    private Set<WeekDays> weekDays;
    private GroupCourseDto groupCourseId;
    private TeacherDto teacherId;
    private Integer classRoomBaseDto;

    public ScheduleUpdateDto(LocalTime startTime, LocalTime endTime, Set<WeekDays> weekDays, GroupCourseDto groupCourseId, TeacherDto teacherId, Integer classRoomBaseDto) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.weekDays = weekDays;
        this.groupCourseId = groupCourseId;
        this.teacherId = teacherId;
        this.classRoomBaseDto = classRoomBaseDto;
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

    public GroupCourseDto getGroupCourseId() {
        return groupCourseId;
    }

    public void setGroupCourseId(GroupCourseDto groupCourseId) {
        this.groupCourseId = groupCourseId;
    }

    public TeacherDto getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(TeacherDto teacherId) {
        this.teacherId = teacherId;
    }

    public Integer getClassRoomBaseDto() {
        return classRoomBaseDto;
    }

    public void setClassRoomBaseDto(Integer classRoomBaseDto) {
        this.classRoomBaseDto = classRoomBaseDto;
    }
}
