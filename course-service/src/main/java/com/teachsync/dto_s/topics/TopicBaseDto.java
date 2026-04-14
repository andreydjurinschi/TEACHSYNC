package com.teachsync.dto_s.topics;

import com.teachsync.domain.TopicTag;

public class TopicBaseDto {
    private Long id;
    private String name;
    private TopicTag topicTag;

    public TopicBaseDto(Long id, String name, TopicTag topicTag) {
        this.id = id;
        this.name = name;
        this.topicTag = topicTag;
    }

    public TopicTag getTopicTag() {
        return topicTag;
    }

    public void setTopicTag(TopicTag topicTag) {
        this.topicTag = topicTag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
