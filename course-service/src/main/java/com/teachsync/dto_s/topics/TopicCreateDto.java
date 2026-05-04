package com.teachsync.dto_s.topics;

import com.teachsync.domain.TopicTag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TopicCreateDto {

    @NotBlank
    @Size(min = 2, max = 80)
    private String name;

    @NotNull
    private TopicTag topicTag;

    public TopicCreateDto() {
    }

    public TopicCreateDto(String name, TopicTag topicTag) {
        this.name = name;
        this.topicTag = topicTag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TopicTag getTopicTag() {
        return topicTag;
    }

    public void setTopicTag(TopicTag topicTag) {
        this.topicTag = topicTag;
    }
}
