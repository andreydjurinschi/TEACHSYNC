package com.teachsync.interaction.kafka;

import com.teachsync.teachsyncevents.constants.KafkaTopics;
import com.teachsync.teachsyncevents.replacements.ReplacementApprovedEvent;
import com.teachsync.teachsyncevents.replacements.ReplacementRequestedEvent;
import com.teachsync.teachsyncevents.replacements.ReplacementStatusChangedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class ReplacementEventProducer {

    private static final Logger log = LoggerFactory.getLogger(ReplacementEventProducer.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public ReplacementEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishReplacementRequested(ReplacementRequestedEvent event) {
        kafkaTemplate.send(KafkaTopics.REPLACEMENT_EVENTS, event.getReplacementRequestId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish ReplacementRequestedEvent: {}", ex.getMessage());
                    }
                });
    }

    public void publishReplacementApproved(ReplacementApprovedEvent event) {
        kafkaTemplate.send(KafkaTopics.REPLACEMENT_EVENTS, event.getReplacementRequestId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish ReplacementApprovedEvent: {}", ex.getMessage());
                    }
                });
    }

    public void publishReplacementStatusChanged(ReplacementStatusChangedEvent event) {
        kafkaTemplate.send(KafkaTopics.REPLACEMENT_EVENTS, event.getReplacementRequestId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish ReplacementStatusChangedEvent: {}", ex.getMessage());
                    }
                });
    }
}
