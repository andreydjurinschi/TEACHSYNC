package com.teachsync.interaction.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teachsync.service.ReplacementRequestService;
import com.teachsync.teachsyncevents.constants.ActionTypes;
import com.teachsync.teachsyncevents.constants.KafkaTopics;
import com.teachsync.teachsyncevents.schedules.ScheduleDeletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ScheduleEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(ScheduleEventConsumer.class);

    private final ObjectMapper objectMapper;
    private final ReplacementRequestService replacementRequestService;

    public ScheduleEventConsumer(ObjectMapper objectMapper, ReplacementRequestService replacementRequestService) {
        this.objectMapper = objectMapper;
        this.replacementRequestService = replacementRequestService;
    }

    @KafkaListener(
            topics = KafkaTopics.SCHEDULE_EVENTS,
            groupId = "replacement-service",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeScheduleEvents(String rawMessage) {
        try {
            var node = objectMapper.readTree(rawMessage);
            String eventType = node.get("actionType").asText();
            if (!ActionTypes.SCHEDULE_DELETED.equals(eventType)) {
                return;
            }
            ScheduleDeletedEvent event = objectMapper.readValue(rawMessage, ScheduleDeletedEvent.class);
            replacementRequestService.deleteByScheduleId(event.getScheduleId());
            log.info("Deleted replacement requests for removed schedule {}", event.getScheduleId());
        } catch (Exception e) {
            log.error("Failed to process schedule cleanup event in replacement-service: {}", e.getMessage(), e);
        }
    }
}
