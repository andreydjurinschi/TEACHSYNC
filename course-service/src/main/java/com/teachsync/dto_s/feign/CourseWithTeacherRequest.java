package com.teachsync.dto_s.feign;

import com.teachsync.interaction.feign.requests.TeacherRequest;

public class CourseWithTeacherRequest {
    private String name;
    private String description;
    private TeacherRequest teacherRequest;

    public CourseWithTeacherRequest(String name, String description, TeacherRequest teacherRequest) {
        this.name = name;
        this.description = description;
        this.teacherRequest = teacherRequest;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TeacherRequest getTeacherRequest() {
        return teacherRequest;
    }

    public void setTeacherRequest(TeacherRequest teacherRequest) {
        this.teacherRequest = teacherRequest;
    }
}
