package com.teachsync.courseservice.services.domain;

import com.teachsync.courseservice.domain.Topic;
import com.teachsync.courseservice.dto_s.topics.TopicBaseDto;
import com.teachsync.courseservice.dto_s.topics.TopicCountedDto;
import com.teachsync.courseservice.mappers.TopicMapper;
import com.teachsync.courseservice.repositories.TopicRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class TopicService {
    private final TopicRepository topicRepository;

    public TopicService(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    public List<TopicBaseDto> getAll(){
        return topicRepository.findAll().stream().map(TopicMapper::mapToDto).toList();
    }

    public TopicBaseDto getById(Long id){
        Topic topic = topicRepository.findById(id).orElseThrow(() -> new NoSuchElementException("this topic does not exist"));
        return TopicMapper.mapToDto(topic);
    }

}
