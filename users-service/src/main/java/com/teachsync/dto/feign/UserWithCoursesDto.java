package com.teachsync.dto.feign;

import com.teachsync.interaction.requests.CourseBaseDto;

import java.util.List;

public class UserWithCoursesDto {
    private Long id;
    private String name;
    private String surname;
    private String email;
    private List<CourseBaseDto> courseNames;
    private boolean available;

    public UserWithCoursesDto(Long id, String name, String surname, String email, List<CourseBaseDto> courseNames, boolean available) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.courseNames = courseNames;
        this.available = available;
    }

    public UserWithCoursesDto() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<CourseBaseDto> getCourseNames() {
        return courseNames;
    }

    public void setCourseNames(List<CourseBaseDto> courseNames) {
        this.courseNames = courseNames;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
