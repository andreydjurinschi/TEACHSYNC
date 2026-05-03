package com.teachsync.interaction.kafka;

import com.teachsync.teachsyncevents.constants.KafkaTopics;
import com.teachsync.teachsyncevents.courses.CourseCreatedEvent;
import com.teachsync.teachsyncevents.courses.CourseGroupEnrolledEvent;
import com.teachsync.teachsyncevents.courses.CourseGroupRelationRemovedEvent;
import com.teachsync.teachsyncevents.courses.CourseTeacherAssignmentRequestedEvent;
import com.teachsync.teachsyncevents.courses.CourseTeacherAssignedEvent;
import com.teachsync.teachsyncevents.courses.CourseTeacherUnassignedEvent;
import com.teachsync.teachsyncevents.courses.CourseTopicRemovedEvent;
import com.teachsync.teachsyncevents.courses.CourseTopicsAddedEvent;
import com.teachsync.teachsyncevents.courses.CourseUpdatedEvent;
import com.teachsync.teachsyncevents.system.SystemAlertEvent;
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

    public void publishCourseTeacherAssignmentRequested(CourseTeacherAssignmentRequestedEvent event) {
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(
                KafkaTopics.COURSE_EVENTS, event.getCourseId().toString(), event
        );
        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed publish event for teacher assignment request in course service: {}", ex.getMessage());
            } else {
                log.info(
                        "CourseTeacherAssignmentRequestedEvent published successfully... courseId: {}, teacherId: {}, notificationCategoryId: {}, notificationCategoryName: {}",
                        event.getCourseId(),
                        event.getTeacherId(),
                        event.getCategoryId(),
                        event.getCategoryName() == null ? "without category" : event.getCategoryName()
                );
            }
        });
    }

    public void publishCourseTeacherUnassigned(CourseTeacherUnassignedEvent event) {
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(
                KafkaTopics.COURSE_EVENTS, event.getCourseId().toString(), event
        );
        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed publish event for teacher unassigning in course service: {}", ex.getMessage());
            } else {
                log.info("CourseTeacherUnassignedEvent published successfully... courseId: {}, previousTeacherId: {}",
                        event.getCourseId(), event.getPreviousTeacherId());
            }
        });
    }

    public void publishCourseEdited(CourseUpdatedEvent event){
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate
                .send(KafkaTopics.COURSE_EVENTS, event.getCourseId().toString(), event);

        future.whenComplete((result, ex) -> {
            if(ex != null){
                log.error("Failed publish event for course updated in  course service: CourseUpdatedEvent");
            }else{
                log.info("CourseUpdatedEvent published successfully... updated course: {}", event.getCourseId());
            }
        });
    }

    public void publishCourseGroupEnrolled(CourseGroupEnrolledEvent event){
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate
                .send(KafkaTopics.COURSE_EVENTS, event.getCourseId().toString(), event);

        future.whenComplete((result, ex) -> {
            if(ex != null){
                log.error("Failed publish event for course groupEnrolled in  course service: CourseGroupEnrolledEvent");
            }else{
                log.info("CourseGroupEnrolledEvent published successfully... courseId: {}; groupId: {}", event.getCourseId(), event.getGroupId());
            }
        });
    }

    public void publishCourseGroupRemoved(CourseGroupRelationRemovedEvent event){
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate
                .send(KafkaTopics.COURSE_EVENTS, event.getCourseId().toString(), event);

        future.whenComplete((result, ex) -> {
            if(ex != null){
                log.error("Failed publish event for course group removed in  course service: CourseGroupRelationRemovedEvent, {}", ex.getMessage());
            }
            log.info("CourseGroupRelationRemovedEvent published successfully... courseId: {}, groupId: {}", event.getCourseId(), event.getGroupId());
        });
    }

    public void publishCourseTopicAdded(CourseTopicsAddedEvent event){
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(
                KafkaTopics.COURSE_EVENTS, event.getCourseId().toString(), event);

        future.whenComplete((result, ex) -> {
            if(ex != null){
                log.error("Failed publish event for course topic add in  course service: CourseTopicsAddedEvent, {}", ex.getMessage());
            }else{
                log.info("CourseTopicsAddedEvent published successfully... courseId: {}, topicId: {}", event.getCourseId(), event.getTopicId());
            }
        });
    }

    public void publishCourseTopicRemoved(CourseTopicRemovedEvent event){
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(
                KafkaTopics.COURSE_EVENTS, event.getCourseId().toString(), event);

        future.whenComplete((result, ex) -> {
            if(ex != null){
                log.error("Failed publish event for course topic remove in  course service: CourseTopicRemovedEvent, {}", ex.getMessage());
            }else{
                log.info("CourseTopicRemovedEvent published successfully... courseId: {}, topicId: {}", event.getCourseId(), event.getTopicId());
            }
        });
    }

    public void publishSystemAlert(SystemAlertEvent event) {
        kafkaTemplate.send(KafkaTopics.SYSTEM_EVENTS, event.getSourceServiceName(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish SystemAlertEvent from course service: {}", ex.getMessage());
                    }
                });
    }

}
