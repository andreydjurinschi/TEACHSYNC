package com.teachsync.interaction.kafka;

import com.teachsync.teachsyncevents.constants.KafkaTopics;
import com.teachsync.teachsyncevents.courses.CourseCreatedEvent;
import com.teachsync.teachsyncevents.courses.CourseTeacherAssignedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class CourseEventProducer {

    private static final Logger log =
            LoggerFactory.getLogger(CourseEventProducer.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public CourseEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishCourseCreated(CourseCreatedEvent event) {
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(
                KafkaTopics.COURSE_EVENTS, event.getCourseId().toString(), event
        );
        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to publish COurseCreateEvent for courseId = {}:{}",
                        event.getCourseId(), ex.getMessage());
            } else {
                log.info("CourseCreatedEvent published -> topic={}, partition={}, offset={}",
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset()
                );
            }
        });
    }

    public void publishCourseTeacherAssigned(CourseTeacherAssignedEvent event){
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(
                KafkaTopics.COURSE_EVENTS, event.getCourseId().toString(), event
        );
        future.whenComplete(((stringObjectSendResult, throwable) -> {
            if(throwable != null){
                log.error("Failed publish event for teacher assigning in  course service");
            }else{
                log.info("CourseAssignedTeacherEvent published -> topic={}, partition={}, offset={}",
                        stringObjectSendResult.getRecordMetadata().topic(),
                        stringObjectSendResult.getRecordMetadata().partition(),
                        stringObjectSendResult.getRecordMetadata().offset()
                );
            }
        }));
    }
}
