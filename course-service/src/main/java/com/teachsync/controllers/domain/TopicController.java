package com.teachsync.controllers.domain;

import com.teachsync.domain.TopicTag;
import com.teachsync.dto_s.topics.TopicBaseDto;
import com.teachsync.services.domain.TopicService;
import feign.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping("/all/by-tag/{tag}")
    public ResponseEntity<List<TopicBaseDto>> getTopicsByTag(@PathVariable TopicTag tag) {
        return ResponseEntity.ok(topicService.getTopicsByTags(tag));
    }

    @PatchMapping("/{id}/tag/{tag}")
    public ResponseEntity<Void> setTag(@PathVariable Long id, @PathVariable TopicTag tag) {
        topicService.setTagToTopic(tag, id);
        return ResponseEntity.ok().build();
    }
}
