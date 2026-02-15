package com.teachsync.services.domain;

import com.teachsync.domain.Topic;
import com.teachsync.dto_s.topics.TopicBaseDto;
import com.teachsync.mappers.TopicMapper;
import com.teachsync.repositories.TopicRepository;
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
