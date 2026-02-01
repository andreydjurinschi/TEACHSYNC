package com.teachsync.dto.feign;

import com.teachsync.interaction.requests.CourseBaseInfoRequest;

import java.util.List;

public class UserWithCoursesDto {
    private String name;
    private String surname;
    private String email;
    private List<CourseBaseInfoRequest> courseNames;

    public UserWithCoursesDto(String name, String surname, String email, List<CourseBaseInfoRequest> courseNames) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.courseNames = courseNames;
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

    public List<CourseBaseInfoRequest> getCourseNames() {
        return courseNames;
    }

    public void setCourseNames(List<CourseBaseInfoRequest> courseNames) {
        this.courseNames = courseNames;
    }
}
