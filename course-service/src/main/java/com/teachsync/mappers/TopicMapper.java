package com.teachsync.mappers;

import com.teachsync.domain.Topic;
import com.teachsync.dto_s.topics.TopicBaseDto;

public class TopicMapper {

    public static TopicBaseDto mapToDto(Topic topic){
        return new TopicBaseDto(topic.getId(), topic.getName(), topic.getTag());
    }
}
