package com.teachsync.controllers.domain;

import com.teachsync.auth.service.JwtService;
import com.teachsync.domain.TopicTag;
import com.teachsync.dto_s.topics.TopicCreateDto;
import com.teachsync.dto_s.topics.TopicBaseDto;
import com.teachsync.services.domain.TopicService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/teachsync/topics")
public class TopicController {

    private final TopicService topicService;
    private final JwtService jwtService;

    public TopicController(TopicService topicRepository, JwtService jwtService) {
        this.topicService = topicRepository;
        this.jwtService = jwtService;
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

    @PostMapping("/create")
    public ResponseEntity<TopicBaseDto> createTopic(@Valid @RequestBody TopicCreateDto dto,
                                                    @RequestHeader("Authorization") String authHeader) {
        assertManagerOrAdmin(authHeader);
        return ResponseEntity.status(HttpStatus.CREATED).body(topicService.create(dto));
    }

    @PatchMapping("/{id}/tag/{tag}")
    public ResponseEntity<Void> setTag(@PathVariable Long id,
                                       @PathVariable TopicTag tag,
                                       @RequestHeader("Authorization") String authHeader) {
        assertManagerOrAdmin(authHeader);
        topicService.setTagToTopic(tag, id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteTopic(@PathVariable Long id,
                                            @RequestHeader("Authorization") String authHeader) {
        assertManagerOrAdmin(authHeader);
        topicService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private void assertManagerOrAdmin(String authHeader) {
        String role = jwtService.extractRole(authHeader.replace("Bearer ", ""));
        if (!"ADMIN".equals(role) && !"MANAGER".equals(role)) {
            throw new org.springframework.security.access.AccessDeniedException("only admins and managers can manage topics");
        }
    }
}
