package com.teachsync.services.domain;

import com.teachsync.domain.Topic;
import com.teachsync.domain.TopicTag;
import com.teachsync.dto_s.topics.TopicCreateDto;
import com.teachsync.dto_s.topics.TopicBaseDto;
import com.teachsync.mappers.TopicMapper;
import com.teachsync.repositories.CourseRepository;
import com.teachsync.repositories.TopicRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class TopicService {
    private final TopicRepository topicRepository;
    private final CourseRepository courseRepository;

    public TopicService(TopicRepository topicRepository, CourseRepository courseRepository) {
        this.topicRepository = topicRepository;
        this.courseRepository = courseRepository;
    }

    public List<TopicBaseDto> getAll(){
        return topicRepository.findAll().stream().map(TopicMapper::mapToDto).toList();
    }

    public TopicBaseDto getById(Long id){
        Topic topic = topicRepository.findById(id).orElseThrow(() -> new NoSuchElementException("this topic does not exist"));
        return TopicMapper.mapToDto(topic);
    }

    public TopicBaseDto create(TopicCreateDto dto) {
        Topic topic = new Topic();
        topic.setName(dto.getName().trim());
        topic.setTag(dto.getTopicTag());
        topicRepository.save(topic);
        return TopicMapper.mapToDto(topic);
    }

    public void setTagToTopic(TopicTag tag, Long topicId){
        Topic topic = topicRepository.findById(topicId).orElseThrow(() -> new NoSuchElementException("this topic does not exist"));
        if(tag != null){
            topic.setTag(tag);
        }
        topicRepository.save(topic);
    }

    public void delete(Long topicId) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new NoSuchElementException("this topic does not exist"));
        courseRepository.deleteAllTopicRelationsForTopic(topicId);
        topicRepository.delete(topic);
    }

    public List<TopicBaseDto> getTopicsByTags(TopicTag topicTag){
        return topicRepository.getTopicByTag(topicTag).stream().map(TopicMapper::mapToDto).toList();
    }

}
