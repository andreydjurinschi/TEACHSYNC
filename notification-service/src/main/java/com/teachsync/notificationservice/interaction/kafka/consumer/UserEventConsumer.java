package com.teachsync.notificationservice.interaction.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teachsync.notificationservice.domain.TargetSubject;
import com.teachsync.notificationservice.enums.TargetRole;
import com.teachsync.notificationservice.service.NotificationService;
import com.teachsync.notificationservice.service.UserActivityService;
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
    private final UserActivityService activityService;
    private final ObjectMapper objectMapper;

    public UserEventConsumer(NotificationService notificationService,
                             UserActivityService activityService,
                             ObjectMapper objectMapper) {
        this.notificationService = notificationService;
        this.activityService = activityService;
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
        activityService.recordForUser(
                event.getUuid(),
                event.getServiceName(),
                ActionTypes.USER_ROLE_CHANGED,
                event.getUserId(),
                null,
                "Users-service",
                "Роль учетной записи изменена",
                "Ваша роль изменена с \"" + event.getPreviousRole() + "\" на \"" + event.getNewRole() + "\".",
                "Предыдущая роль: " + event.getPreviousRole() + ". Новая роль: " + event.getNewRole() + ".",
                "/profile"
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
        activityService.recordForRole(
                event.getUuid(),
                event.getServiceName(),
                ActionTypes.USER_CREATED,
                TargetRole.ADMIN,
                event.getUserId(),
                event.getFirstName() + " " + event.getLastName(),
                "Создан пользователь",
                "Создан пользователь " + event.getFirstName() + " " + event.getLastName() + ".",
                "Email: " + event.getUserEmail() + ". Роль: " + event.getUserRole() + ".",
                "/users"
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
        activityService.recordForRole(
                event.getUuid(),
                event.getServiceName(),
                ActionTypes.USER_DELETED,
                TargetRole.ADMIN,
                event.getUserId(),
                event.getFirstName() + " " + event.getLastName(),
                "Пользователь удален",
                message,
                "Email: " + event.getEmail() + ". Роль: " + event.getRole() + ".",
                "/users"
        );
        notificationService.saveForRole(
                event.getUuid(),
                event.getServiceName(),
                TargetSubject.USER_DELETED,
                TargetRole.MANAGER,
                "Пользователь удален",
                message
        );
        activityService.recordForRole(
                event.getUuid(),
                event.getServiceName(),
                ActionTypes.USER_DELETED,
                TargetRole.MANAGER,
                event.getUserId(),
                event.getFirstName() + " " + event.getLastName(),
                "Пользователь удален",
                message,
                "Email: " + event.getEmail() + ". Роль: " + event.getRole() + ".",
                "/users"
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
        activityService.recordForUser(
                event.getUuid(),
                event.getServiceName(),
                ActionTypes.USER_SPEC_ADDED,
                event.getUserId(),
                null,
                "Users-service",
                "Добавлена специализация",
                "Вам добавлена специализация \"" + event.getSpecializationName() + "\".",
                "Специализация используется при подборе преподавателей для курсов и замен.",
                "/profile"
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
        activityService.recordForUser(
                event.getUuid(),
                event.getServiceName(),
                ActionTypes.USER_SPEC_DELETED,
                event.getUserId(),
                null,
                "Users-service",
                "Специализация удалена",
                "У вас удалена специализация \"" + event.getCategoryName() + "\".",
                "Удаленная специализация больше не учитывается при подборе преподавателей.",
                "/profile"
        );
        log.info("Saved USER_SPEC_DELETED notification for teacher {}", event.getUserId());
    }
}
