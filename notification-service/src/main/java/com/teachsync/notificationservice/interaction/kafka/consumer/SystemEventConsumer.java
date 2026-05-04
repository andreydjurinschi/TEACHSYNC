package com.teachsync.notificationservice.interaction.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teachsync.notificationservice.domain.TargetSubject;
import com.teachsync.notificationservice.enums.TargetRole;
import com.teachsync.notificationservice.service.NotificationService;
import com.teachsync.teachsyncevents.constants.ActionTypes;
import com.teachsync.teachsyncevents.constants.KafkaTopics;
import com.teachsync.teachsyncevents.system.SystemAlertEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class SystemEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(SystemEventConsumer.class);

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    public SystemEventConsumer(NotificationService notificationService, ObjectMapper objectMapper) {
        this.notificationService = notificationService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(
            topics = KafkaTopics.SYSTEM_EVENTS,
            groupId = "notification-service",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeSystemEvents(String rawMessage) {
        try {
            var node = objectMapper.readTree(rawMessage);
            String eventType = node.get("actionType").asText();
            if (ActionTypes.SYSTEM_ALERT.equals(eventType)) {
                handleSystemAlert(objectMapper.readValue(rawMessage, SystemAlertEvent.class));
            } else {
                log.info("Unsupported system event type: {}", eventType);
            }
        } catch (Exception e) {
            log.error("Failed to process system event: {}", e.getMessage(), e);
        }
    }

    private void handleSystemAlert(SystemAlertEvent event) {
        notificationService.saveForRole(
                event.getUuid(),
                event.getServiceName(),
                TargetSubject.SYSTEM_ALERT,
                TargetRole.ADMIN,
                "Проблема межсервисного взаимодействия",
                buildMessage(event),
                "/profile"
        );
    }

    private String buildMessage(SystemAlertEvent event) {
        return "Сервис: " + event.getSourceServiceName()
                + ". Операция: " + event.getOperation()
                + ". Зависимость: " + event.getDependency()
                + ". Важность: " + event.getSeverity()
                + ". " + event.getMessage()
                + ". Детали: " + event.getTechnicalDetails();
    }
}
