package com.teachsync.notificationservice.interaction.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teachsync.notificationservice.domain.TargetSubject;
import com.teachsync.notificationservice.service.NotificationService;
import com.teachsync.teachsyncevents.constants.ActionTypes;
import com.teachsync.teachsyncevents.constants.KafkaTopics;
import com.teachsync.teachsyncevents.replacements.ReplacementApprovedEvent;
import com.teachsync.teachsyncevents.replacements.ReplacementRequestedEvent;
import com.teachsync.teachsyncevents.replacements.ReplacementStatusChangedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ReplacementEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(ReplacementEventConsumer.class);

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    public ReplacementEventConsumer(NotificationService notificationService, ObjectMapper objectMapper) {
        this.notificationService = notificationService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(
            topics = KafkaTopics.REPLACEMENT_EVENTS,
            groupId = "notification-service",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeReplacementEvents(String rawMessage) {
        try {
            var node = objectMapper.readTree(rawMessage);
            String eventType = node.get("actionType").asText();
            switch (eventType) {
                case ActionTypes.REPLACEMENT_REQUESTED -> handleRequested(objectMapper.readValue(rawMessage, ReplacementRequestedEvent.class));
                case ActionTypes.REPLACEMENT_APPROVED -> handleApproved(objectMapper.readValue(rawMessage, ReplacementApprovedEvent.class));
                case ActionTypes.REPLACEMENT_STATUS_CHANGED -> handleStatusChanged(objectMapper.readValue(rawMessage, ReplacementStatusChangedEvent.class));
                default -> log.info("Unsupported replacement event type: {}", eventType);
            }
        } catch (Exception e) {
            log.error("Failed to process replacement event: {}", e.getMessage(), e);
        }
    }

    private void handleRequested(ReplacementRequestedEvent event) {
        notificationService.saveForUser(
                event.getUuid(),
                event.getServiceName(),
                TargetSubject.REPLACEMENT_REQUESTED,
                event.getCandidateTeacherId(),
                "Можно помочь с заменой",
                buildRequestedMessage(event),
                "/profile/schedules?replacementRequestId=" + event.getReplacementRequestId()
        );
        log.info("Saved REPLACEMENT_REQUESTED notification for teacher {}", event.getCandidateTeacherId());
    }

    private void handleApproved(ReplacementApprovedEvent event) {
        notificationService.saveForUser(
                event.getUuid(),
                event.getServiceName(),
                TargetSubject.REPLACEMENT_APPROVED,
                event.getTeacherRequestedId(),
                "Замена найдена",
                "Вашу пару по курсу \"" + event.getCourseName() + "\" для группы \"" + event.getGroupName()
                        + "\" " + event.getLessonDate() + " заменит "
                        + event.getApprovedTeacherName() + " (" + event.getApprovedTeacherEmail() + ").",
                "/profile/schedules"
        );
        notificationService.saveForUser(
                event.getUuid(),
                event.getServiceName(),
                TargetSubject.REPLACEMENT_APPROVED,
                event.getApprovedTeacherId(),
                "Вы подтвердили замену",
                "Вы заменяете коллегу по курсу \"" + event.getCourseName() + "\" для группы \"" + event.getGroupName()
                        + "\" " + event.getLessonDate() + " с " + event.getStartTime() + " до " + event.getEndTime() + ".",
                "/profile/schedules"
        );
        log.info("Saved REPLACEMENT_APPROVED notifications for request {}", event.getReplacementRequestId());
    }

    private void handleStatusChanged(ReplacementStatusChangedEvent event) {
        Long targetTeacherId = event.getTargetTeacherId() != null
                ? event.getTargetTeacherId()
                : event.getTeacherRequestedId();

        notificationService.saveForUser(
                event.getUuid(),
                event.getServiceName(),
                TargetSubject.REPLACEMENT_STATUS_CHANGED,
                targetTeacherId,
                event.getTitle() == null || event.getTitle().isBlank()
                        ? "Статус замены изменен"
                        : event.getTitle(),
                event.getMessage(),
                event.getActionUrl() == null || event.getActionUrl().isBlank()
                        ? "/profile/replacements"
                        : event.getActionUrl()
        );
        log.info(
                "Saved REPLACEMENT_STATUS_CHANGED notification for request {} and teacher {}",
                event.getReplacementRequestId(),
                targetTeacherId
        );
    }

    private String buildRequestedMessage(ReplacementRequestedEvent event) {
        StringBuilder builder = new StringBuilder()
                .append("Коллеге нужна замена по курсу \"")
                .append(event.getCourseName())
                .append("\" для группы \"")
                .append(event.getGroupName())
                .append("\" ")
                .append(event.getLessonDate())
                .append(" с ")
                .append(event.getStartTime())
                .append(" до ")
                .append(event.getEndTime());

        if (event.getClassRoomName() != null && !event.getClassRoomName().isBlank()) {
            builder.append(". Аудитория: \"").append(event.getClassRoomName()).append("\"");
        }
        if (event.getReason() != null && !event.getReason().isBlank()) {
            builder.append(". Причина: ").append(event.getReason());
        }

        return builder.toString();
    }
}
