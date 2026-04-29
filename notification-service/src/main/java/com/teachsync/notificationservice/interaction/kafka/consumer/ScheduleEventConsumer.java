package com.teachsync.notificationservice.interaction.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teachsync.notificationservice.domain.TargetSubject;
import com.teachsync.notificationservice.service.NotificationService;
import com.teachsync.teachsyncevents.constants.ActionTypes;
import com.teachsync.teachsyncevents.constants.KafkaTopics;
import com.teachsync.teachsyncevents.schedules.ScheduleCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ScheduleEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(ScheduleEventConsumer.class);

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    public ScheduleEventConsumer(NotificationService notificationService, ObjectMapper objectMapper) {
        this.notificationService = notificationService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(
            topics = KafkaTopics.SCHEDULE_EVENTS,
            groupId = "notification-service",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeScheduleEvents(String rawMessage) {
        try {
            var node = objectMapper.readTree(rawMessage);
            String eventType = node.get("actionType").asText();
            switch (eventType) {
                case ActionTypes.SCHEDULE_CREATED -> handleScheduleCreated(objectMapper.readValue(rawMessage, ScheduleCreatedEvent.class));
                default -> log.info("Unsupported schedule event type: {}", eventType);
            }
        } catch (Exception e) {
            log.error("Failed to process schedule event: {}", e.getMessage(), e);
        }
    }

    private void handleScheduleCreated(ScheduleCreatedEvent event) {
        String days = event.getWeekDays() == null ? "" : String.join(", ", event.getWeekDays());
        notificationService.saveForUser(
                event.getUuid(),
                event.getServiceName(),
                TargetSubject.SCHEDULE_CREATED,
                event.getTeacherId(),
                "Создано расписание",
                "Для группы \"" + event.getGroupName() + "\" по курсу \"" + event.getCourseName()
                        + "\" создано расписание: " + days + ", "
                        + event.getStartTime() + "-" + event.getEndTime()
                        + ", аудитория \"" + event.getClassRoomName() + "\"."
        );
        log.info("Saved SCHEDULE_CREATED notification for teacher {}", event.getTeacherId());
    }
}
