package com.teachsync.notificationservice.interaction.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teachsync.notificationservice.service.NotificationService;
import com.teachsync.teachsyncevents.base.BaseEvent;
import com.teachsync.teachsyncevents.constants.ActionTypes;
import com.teachsync.teachsyncevents.constants.KafkaTopics;
import com.teachsync.teachsyncevents.users.UserCreatedEvent;
import com.teachsync.teachsyncevents.users.UserDeletedEvent;
import com.teachsync.teachsyncevents.users.UserRoleChangedEvent;
import com.teachsync.teachsyncevents.users.UserSpecializationAddedEvent;
import com.teachsync.teachsyncevents.users.UserSpecializationRemovedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class UserEventConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(UserEventConsumer.class);

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    public UserEventConsumer(NotificationService notificationService, ObjectMapper objectMapper) {
        this.notificationService = notificationService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(
            topics = KafkaTopics.USER_EVENTS,
            groupId = "notification-service",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeUserEvents(String rawMessage) {
        try {
            log.info("Consuming user events from Kafka topic: {}", rawMessage);
            var node = objectMapper.readTree(rawMessage);
            String eventType = node.get("actionType").asText();
            switch (eventType) {
                case ActionTypes.USER_ROLE_CHANGED -> {
                    UserRoleChangedEvent userRoleChangedEvent = objectMapper.readValue(rawMessage, UserRoleChangedEvent.class);
                    log.info("RoleChangingNotification would be sent to: {}, {}, {}",
                            userRoleChangedEvent.getUserId(),
                            userRoleChangedEvent.getPreviousRole(),
                            userRoleChangedEvent.getNewRole());
                }
                case ActionTypes.USER_CREATED -> {
                    UserCreatedEvent userCreatedEvent = objectMapper.readValue(rawMessage, UserCreatedEvent.class);
                    log.info("UserCreatedEventNotification would be sent to All ADMINS: deleted use info: {}", userCreatedEvent.getUserId());
                }
                case ActionTypes.USER_DELETED -> {
                    UserDeletedEvent userDeletedEvent = objectMapper.readValue(rawMessage, UserDeletedEvent.class);
                    log.info("UserCreatedEventNotification would be sent to All ADMINS and MANAGERS: deleted use id: {}", userDeletedEvent.getUserId());
                }
                case ActionTypes.USER_SPEC_ADDED -> {
                    UserSpecializationAddedEvent userSpecializationAddedEvent = objectMapper.readValue(rawMessage, UserSpecializationAddedEvent.class);
                    log.info("UserSpecializationAddedEvent notification would be sent to TEACHER with id {}", userSpecializationAddedEvent.getUserId());
                }
                case ActionTypes.USER_SPEC_DELETED -> {
                    UserSpecializationRemovedEvent userSpecializationRemovedEvent  = objectMapper.readValue(rawMessage, UserSpecializationRemovedEvent.class);
                    log.info("UserSpecializationRemovedEvent notification would be sent to teacher with id: {}", userSpecializationRemovedEvent.getUserId());
                }
            }
        } catch (Exception e) {
            log.error("Error handling userRoleChangedEvent: {}", e.getMessage());
        }
    }

}
