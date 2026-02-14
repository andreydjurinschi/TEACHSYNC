package com.teachsync.courseservice.mappers;

import com.teachsync.courseservice.domain.Topic;
import com.teachsync.courseservice.dto_s.topics.TopicBaseDto;

public class TopicMapper {

    public static TopicBaseDto mapToDto(Topic topic){
        return new TopicBaseDto(topic.getName());
    }
}
