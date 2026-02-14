package com.teachsync.courseservice.dto_s.topics;

public class TopicCountedDto {
    private String name;
    private Integer count;

    public TopicCountedDto(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
