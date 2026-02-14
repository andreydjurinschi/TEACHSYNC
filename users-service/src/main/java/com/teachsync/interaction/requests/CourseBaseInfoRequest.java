package com.teachsync.interaction.requests;

public class CourseBaseInfoRequest {

    private Long id;
    private String name;

    public CourseBaseInfoRequest(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public CourseBaseInfoRequest() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
