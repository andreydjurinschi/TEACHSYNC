package com.teachsync.notificationservice.interaction.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teachsync.notificationservice.domain.TargetSubject;
import com.teachsync.notificationservice.enums.TargetRole;
import com.teachsync.notificationservice.service.NotificationService;
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

    private static final Logger log = LoggerFactory.getLogger(UserEventConsumer.class);

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
            var node = objectMapper.readTree(rawMessage);
            String eventType = node.get("actionType").asText();
            switch (eventType) {
                case ActionTypes.USER_ROLE_CHANGED -> handleUserRoleChanged(objectMapper.readValue(rawMessage, UserRoleChangedEvent.class));
                case ActionTypes.USER_CREATED -> handleUserCreated(objectMapper.readValue(rawMessage, UserCreatedEvent.class));
                case ActionTypes.USER_DELETED -> handleUserDeleted(objectMapper.readValue(rawMessage, UserDeletedEvent.class));
                case ActionTypes.USER_SPEC_ADDED -> handleUserSpecializationAdded(objectMapper.readValue(rawMessage, UserSpecializationAddedEvent.class));
                case ActionTypes.USER_SPEC_DELETED -> handleUserSpecializationRemoved(objectMapper.readValue(rawMessage, UserSpecializationRemovedEvent.class));
                default -> log.info("Unsupported user event type: {}", eventType);
            }
        } catch (Exception e) {
            log.error("Error handling user event: {}", e.getMessage(), e);
        }
    }

    private void handleUserRoleChanged(UserRoleChangedEvent event) {
        notificationService.saveForUser(
                event.getUuid(),
                event.getServiceName(),
                TargetSubject.USER_ROLE_CHANGED,
                event.getUserId(),
                "Роль учетной записи изменена",
                "Ваша роль изменена с \"" + event.getPreviousRole() + "\" на \"" + event.getNewRole() + "\"."
        );
        log.info("Saved USER_ROLE_CHANGED notification for user {}", event.getUserId());
    }

    private void handleUserCreated(UserCreatedEvent event) {
        notificationService.saveForRole(
                event.getUuid(),
                event.getServiceName(),
                TargetSubject.USER_CREATED,
                TargetRole.ADMIN,
                "Создан новый пользователь",
                "Создан пользователь " + event.getFirstName() + " " + event.getLastName() + " с ролью " + event.getUserRole() + "."
        );
        log.info("Saved USER_CREATED notification for admins: {}", event.getUserId());
    }

    private void handleUserDeleted(UserDeletedEvent event) {
        String message = "Удален пользователь " + event.getFirstName() + " " + event.getLastName() + " с ролью " + event.getRole() + ".";
        notificationService.saveForRole(
                event.getUuid(),
                event.getServiceName(),
                TargetSubject.USER_DELETED,
                TargetRole.ADMIN,
                "Пользователь удален",
                message
        );
        notificationService.saveForRole(
                event.getUuid(),
                event.getServiceName(),
                TargetSubject.USER_DELETED,
                TargetRole.MANAGER,
                "Пользователь удален",
                message
        );
        log.info("Saved USER_DELETED notifications for admins and managers: {}", event.getUserId());
    }

    private void handleUserSpecializationAdded(UserSpecializationAddedEvent event) {
        notificationService.saveForUser(
                event.getUuid(),
                event.getServiceName(),
                TargetSubject.USER_SPECIALIZATION_ADDED,
                event.getUserId(),
                "Добавлена специализация",
                "Вам добавлена специализация \"" + event.getSpecializationName() + "\"."
        );
        log.info("Saved USER_SPEC_ADDED notification for teacher {}", event.getUserId());
    }

    private void handleUserSpecializationRemoved(UserSpecializationRemovedEvent event) {
        notificationService.saveForUser(
                event.getUuid(),
                event.getServiceName(),
                TargetSubject.USER_SPECIALIZATION_REMOVED,
                event.getUserId(),
                "Специализация удалена",
                "У вас удалена специализация \"" + event.getCategoryName() + "\"."
        );
        log.info("Saved USER_SPEC_DELETED notification for teacher {}", event.getUserId());
    }
}
