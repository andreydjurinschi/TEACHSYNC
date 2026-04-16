package com.teachsync.dto_s.groups;

import com.teachsync.dto_s.courses.CourseShortDto;

import java.time.LocalDate;
import java.util.Set;

public class GroupWithCoursesDto {
    private Long id;
    private String name;
    private LocalDate openDate;
    private int capacity;
    private Set<CourseShortDto> courses;

    public GroupWithCoursesDto(Long id, String name, LocalDate openDate, int capacity, Set<CourseShortDto> courses) {
        this.id = id;
        this.name = name;
        this.openDate = openDate;
        this.capacity = capacity;
        this.courses = courses;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getOpenDate() {
        return openDate;
    }

    public void setOpenDate(LocalDate openDate) {
        this.openDate = openDate;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
