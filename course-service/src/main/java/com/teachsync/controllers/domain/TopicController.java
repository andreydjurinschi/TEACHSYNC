package com.teachsync.controllers.domain;

import com.teachsync.dto_s.topics.TopicBaseDto;
import com.teachsync.services.domain.TopicService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/teachsync/topics")
public class TopicController {

    private final TopicService topicService;

    public TopicController(TopicService topicRepository) {
        this.topicService = topicRepository;
    }

    @GetMapping("/all")
    public ResponseEntity<List<TopicBaseDto>> getAllTopics() {
        return ResponseEntity.ok().body(topicService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TopicBaseDto> getTopicById(@PathVariable Long id) {
        return ResponseEntity.ok().body(topicService.getById(id));
    }
}
