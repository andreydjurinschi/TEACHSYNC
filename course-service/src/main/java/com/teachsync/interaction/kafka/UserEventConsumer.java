package com.teachsync.interaction.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teachsync.interaction.feign.requests.Role;
import com.teachsync.services.domain.CourseService;
import com.teachsync.teachsyncevents.constants.ActionTypes;
import com.teachsync.teachsyncevents.constants.KafkaTopics;
import com.teachsync.teachsyncevents.users.UserDeletedEvent;
import com.teachsync.teachsyncevents.users.UserRoleChangedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class UserEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(UserEventConsumer.class);

    private final ObjectMapper objectMapper;
    private final CourseService courseService;

    public UserEventConsumer(ObjectMapper objectMapper, CourseService courseService) {
        this.objectMapper = objectMapper;
        this.courseService = courseService;
    }

    @KafkaListener(
            topics = KafkaTopics.USER_EVENTS,
            groupId = "course-service",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeUserEvents(String rawMessage) {
        try {
            var node = objectMapper.readTree(rawMessage);
            String eventType = node.get("actionType").asText();
            switch (eventType) {
                case ActionTypes.USER_DELETED -> handleUserDeleted(objectMapper.readValue(rawMessage, UserDeletedEvent.class));
                case ActionTypes.USER_ROLE_CHANGED -> handleUserRoleChanged(objectMapper.readValue(rawMessage, UserRoleChangedEvent.class));
                default -> log.info("Ignoring user event {} in course-service", eventType);
            }
        } catch (Exception e) {
            log.error("Failed to process user event in course-service: {}", e.getMessage(), e);
        }
    }

    private void handleUserDeleted(UserDeletedEvent event) {
        if (!Role.TEACHER.name().equalsIgnoreCase(event.getRole())) {
            return;
        }
        int affectedCourses = courseService.unassignTeacherFromAllCourses(event.getUserId());
        log.info("Processed USER_DELETED event for teacher {}. Unassigned {} course(s)", event.getUserId(), affectedCourses);
    }

    private void handleUserRoleChanged(UserRoleChangedEvent event) {
        if (!Role.TEACHER.name().equalsIgnoreCase(event.getPreviousRole())) {
            return;
        }
        if (Role.TEACHER.name().equalsIgnoreCase(event.getNewRole())) {
            return;
        }
        int affectedCourses = courseService.unassignTeacherFromAllCourses(event.getUserId());
        log.info("Processed USER_ROLE_CHANGED event for user {}. Unassigned {} course(s)", event.getUserId(), affectedCourses);
    }
}
