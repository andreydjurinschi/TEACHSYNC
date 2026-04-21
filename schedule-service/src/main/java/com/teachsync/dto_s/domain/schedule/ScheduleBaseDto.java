package com.teachsync.dto_s.domain.schedule;


import com.teachsync.domain.ScheduleDay;
import com.teachsync.dto_s.domain.class_room.ClassRoomBaseDto;
import com.teachsync.interation.feign.requests.GroupCourseBaseInfoRequest;
import com.teachsync.interation.feign.requests.TeacherBaseInfoRequest;

import java.time.LocalTime;
import java.util.Set;

public class ScheduleBaseDto {
    private Long id;
    private LocalTime startTime;
    private LocalTime endTime;
    private Set<String> weekDays;
    private GroupCourseBaseInfoRequest groupCourseDto;
    private TeacherBaseInfoRequest teacherDto;
    private ClassRoomBaseDto classRoomBaseDto;

    public ScheduleBaseDto(Long id, LocalTime startTime, LocalTime endTime, Set<String> weekDays, GroupCourseBaseInfoRequest groupCourseDto, TeacherBaseInfoRequest teacherDto, ClassRoomBaseDto classRoomBaseDto) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.weekDays = weekDays;
        this.groupCourseDto = groupCourseDto;
        this.teacherDto = teacherDto;
        this.classRoomBaseDto = classRoomBaseDto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public GroupCourseBaseInfoRequest getGroupCourseDto() {
        return groupCourseDto;
    }

    public void setGroupCourseDto(GroupCourseBaseInfoRequest groupCourseDto) {
        this.groupCourseDto = groupCourseDto;
    }

    public TeacherBaseInfoRequest getTeacherDto() {
        return teacherDto;
    }

    public void setTeacherDto(TeacherBaseInfoRequest teacherDto) {
        this.teacherDto = teacherDto;
    }

    public ClassRoomBaseDto getClassRoomBaseDto() {
        return classRoomBaseDto;
    }

    public void setClassRoomBaseDto(ClassRoomBaseDto classRoomBaseDto) {
        this.classRoomBaseDto = classRoomBaseDto;
    }
}
