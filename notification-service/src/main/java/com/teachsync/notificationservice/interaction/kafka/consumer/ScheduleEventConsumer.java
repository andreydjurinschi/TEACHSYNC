package com.teachsync.notificationservice.interaction.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teachsync.notificationservice.domain.TargetSubject;
import com.teachsync.notificationservice.service.NotificationService;
import com.teachsync.notificationservice.service.UserActivityService;
import com.teachsync.teachsyncevents.constants.ActionTypes;
import com.teachsync.teachsyncevents.constants.KafkaTopics;
import com.teachsync.teachsyncevents.schedules.ScheduleCreatedEvent;
import com.teachsync.teachsyncevents.schedules.ScheduleUpdatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ScheduleEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(ScheduleEventConsumer.class);

    private final NotificationService notificationService;
    private final UserActivityService activityService;
    private final ObjectMapper objectMapper;

    public ScheduleEventConsumer(NotificationService notificationService,
                                 UserActivityService activityService,
                                 ObjectMapper objectMapper) {
        this.notificationService = notificationService;
        this.activityService = activityService;
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
                case ActionTypes.SCHEDULE_UPDATED -> handleScheduleUpdated(objectMapper.readValue(rawMessage, ScheduleUpdatedEvent.class));
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
        activityService.recordForUser(
                event.getUuid(),
                event.getServiceName(),
                ActionTypes.SCHEDULE_CREATED,
                event.getTeacherId(),
                null,
                "Schedule-service",
                "Создано расписание",
                "Создано занятие по курсу \"" + event.getCourseName() + "\" для группы \"" + event.getGroupName() + "\".",
                "Время: " + days + ", " + event.getStartTime() + "-" + event.getEndTime()
                        + ". Аудитория: " + event.getClassRoomName() + ".",
                "/profile/schedules"
        );
        log.info("Saved SCHEDULE_CREATED notification for teacher {}", event.getTeacherId());
    }

    private void handleScheduleUpdated(ScheduleUpdatedEvent event) {
        String days = event.getWeekDays() == null ? "" : String.join(", ", event.getWeekDays());
        String message = "Расписание по курсу \"" + event.getCourseName()
                + "\" для группы \"" + event.getGroupName()
                + "\" изменено пользователем " + event.getChangedByName() + ".\n"
                + "Новое расписание: " + days + ", "
                + event.getStartTime() + "-" + event.getEndTime()
                + ", аудитория \"" + event.getClassRoomName() + "\".\n"
                + "Что изменилось: " + event.getChangeSummary();

        notificationService.saveForUser(
                event.getUuid(),
                event.getServiceName(),
                TargetSubject.SCHEDULE_UPDATED,
                event.getTeacherId(),
                "Расписание изменено",
                message,
                "/profile/schedules"
        );
        activityService.recordForUser(
                event.getUuid(),
                event.getServiceName(),
                ActionTypes.SCHEDULE_UPDATED,
                event.getTeacherId(),
                event.getChangedByUserId(),
                event.getChangedByName(),
                "Расписание изменено",
                "Расписание по курсу \"" + event.getCourseName() + "\" изменено.",
                "Изменил: " + event.getChangedByName()
                        + ". Новое расписание: " + days + ", " + event.getStartTime() + "-" + event.getEndTime()
                        + ", аудитория \"" + event.getClassRoomName() + "\". Что изменилось: " + event.getChangeSummary(),
                "/profile/schedules"
        );

        if (event.getPreviousTeacherId() != null && !event.getPreviousTeacherId().equals(event.getTeacherId())) {
            notificationService.saveForUser(
                    event.getUuid(),
                    event.getServiceName(),
                    TargetSubject.SCHEDULE_UPDATED,
                    event.getPreviousTeacherId(),
                    "Расписание изменено",
                    "Вы больше не назначены на занятие по курсу \"" + event.getCourseName()
                            + "\" для группы \"" + event.getGroupName()
                            + "\". Изменил: " + event.getChangedByName()
                            + ". Что изменилось: " + event.getChangeSummary(),
                    "/profile/schedules"
            );
            activityService.recordForUser(
                    event.getUuid(),
                    event.getServiceName(),
                    ActionTypes.SCHEDULE_UPDATED,
                    event.getPreviousTeacherId(),
                    event.getChangedByUserId(),
                    event.getChangedByName(),
                    "Расписание изменено",
                    "Вы больше не назначены на занятие по курсу \"" + event.getCourseName() + "\".",
                    "Изменил: " + event.getChangedByName()
                            + ". Группа: " + event.getGroupName()
                            + ". Что изменилось: " + event.getChangeSummary(),
                    "/profile/schedules"
            );
        }

        log.info("Saved SCHEDULE_UPDATED notification for teacher {}", event.getTeacherId());
    }
}
