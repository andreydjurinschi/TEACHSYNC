package com.teachsync.courseservice.dto_s.groups;

import com.teachsync.courseservice.dto_s.courses.CourseShortDto;

import java.time.LocalDate;
import java.util.Set;

public class GroupWithCoursesDto {
    private String name;
    private LocalDate date;
    private int capacity;
    private Set<CourseShortDto> courses;

    public GroupWithCoursesDto(String name, LocalDate date, int capacity, Set<CourseShortDto> courses) {
        this.name = name;
        this.date = date;
        this.capacity = capacity;
        this.courses = courses;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public Set<CourseShortDto> getCourses() {
        return courses;
    }

    public void setCourses(Set<CourseShortDto> courses) {
        this.courses = courses;
    }
}
