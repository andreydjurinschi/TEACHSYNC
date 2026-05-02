package com.teachsync.interation.kafka;

import com.teachsync.teachsyncevents.constants.KafkaTopics;
import com.teachsync.teachsyncevents.schedules.ScheduleCreatedEvent;
import com.teachsync.teachsyncevents.schedules.ScheduleUpdatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class ScheduleEventProducer {

    private static final Logger log = LoggerFactory.getLogger(ScheduleEventProducer.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public ScheduleEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishScheduleCreated(ScheduleCreatedEvent event) {
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(
                KafkaTopics.SCHEDULE_EVENTS, event.getScheduleId().toString(), event
        );
        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed publish event for schedule created in schedule service: {}", ex.getMessage());
            } else {
                log.info("ScheduleCreatedEvent published successfully... scheduleId: {}, teacherId: {}",
                        event.getScheduleId(), event.getTeacherId());
            }
        });
    }

    public void publishScheduleUpdated(ScheduleUpdatedEvent event) {
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(
                KafkaTopics.SCHEDULE_EVENTS, event.getScheduleId().toString(), event
        );
        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed publish event for schedule updated in schedule service: {}", ex.getMessage());
            } else {
                log.info("ScheduleUpdatedEvent published successfully... scheduleId: {}, teacherId: {}, changedBy: {}",
                        event.getScheduleId(), event.getTeacherId(), event.getChangedByUserId());
            }
        });
    }
}
