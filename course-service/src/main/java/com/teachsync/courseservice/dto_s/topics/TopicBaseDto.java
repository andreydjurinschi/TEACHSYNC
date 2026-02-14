package com.teachsync.courseservice.dto_s.topics;

public class TopicBaseDto {
    private String name;

    public TopicBaseDto(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
