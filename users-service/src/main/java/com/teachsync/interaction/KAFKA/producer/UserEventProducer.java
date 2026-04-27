package com.teachsync.interaction.KAFKA.producer;

import com.teachsync.teachsyncevents.constants.KafkaTopics;
import com.teachsync.teachsyncevents.users.UserCreatedEvent;
import com.teachsync.teachsyncevents.users.UserDeletedEvent;
import com.teachsync.teachsyncevents.users.UserRoleChangedEvent;
import com.teachsync.teachsyncevents.users.UserSpecializationAddedEvent;
import com.teachsync.teachsyncevents.users.UserSpecializationRemovedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class UserEventProducer {

    private static final Logger log = LoggerFactory.getLogger(UserEventProducer.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public UserEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishUserRoleChanged(UserRoleChangedEvent event) {
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(
                KafkaTopics.USER_EVENTS, event.getUserId().toString(), event
        );
        future.whenComplete((r, e) -> {
            if (e != null) {
                log.error("Error while sending user event to kafka topic", e);
            } else {
                log.info("UserRoleChangedEvent published successfully: id: {}, prev: {}, new: {}",
                        event.getUserId(), event.getPreviousRole(), event.getNewRole()
                );
            }
        });
    }

    public void publishUserCreated(UserCreatedEvent event) {
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(
                KafkaTopics.USER_EVENTS, event.getUserId().toString(), event);

        future.whenComplete((r, e) -> {
            if (e != null) {
                log.error("Error while sending user event to kafka topic", e);
            } else {
                log.info("UserCreatedEvent published successfully: {},{},{},{}, {}",
                        event.getUserId(), event.getFirstName(), event.getLastName(), event.getUserEmail(), event.getUserRole()
                );
            }
        });
    }

    public void publishUserDeleted(UserDeletedEvent event) {
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(
                KafkaTopics.USER_EVENTS, event.getUserId().toString(), event);

        future.whenComplete((r, e) -> {
            if (e != null) {
                log.error("Error while sending user event to kafka topic", e);
            } else {
                log.info("UserDeletedEvent published successfully: {},{}",
                        event.getUserId(), event.getEmail()
                );
            }
        });
    }

    public void publishUserSpecializationAdded(UserSpecializationAddedEvent event) {
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate
                .send(KafkaTopics.USER_EVENTS, event.getUserId().toString(), event);

        future.whenComplete((r, e) -> {
            if (e != null) {
                log.error("Error while sending user event to kafka topic", e);
            } else {
                log.info("UserSpecializationAddedEvent published successfully: {}, {}",
                        event.getUserId(), event.getSpecializationName()
                );
            }
        });
    }

    public void publishUserSpecializationRemoved(UserSpecializationRemovedEvent event) {
        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(KafkaTopics.USER_EVENTS, event.getUserId().toString(), event);

        future.whenComplete((r, e) -> {
            if (e != null) {
                log.error("Error while sending user event to kafka topic", e);
            } else {
                log.info("UserSpecializationRemovedEvent published successfully: user id: {}, spec name: {} ",
                        event.getUserId(), event.getCategoryName()
                );
            }
        });
    }
}
