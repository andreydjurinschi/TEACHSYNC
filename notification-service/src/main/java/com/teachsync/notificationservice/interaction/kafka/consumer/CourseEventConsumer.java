package com.teachsync.notificationservice.interaction.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teachsync.notificationservice.domain.Notification;
import com.teachsync.notificationservice.domain.TargetSubject;
import com.teachsync.notificationservice.enums.TargetRole;
import com.teachsync.notificationservice.service.NotificationService;
import com.teachsync.teachsyncevents.constants.ActionTypes;
import com.teachsync.teachsyncevents.constants.KafkaTopics;
import com.teachsync.teachsyncevents.courses.CourseCreatedEvent;
import com.teachsync.teachsyncevents.courses.CourseGroupEnrolledEvent;
import com.teachsync.teachsyncevents.courses.CourseGroupRelationRemovedEvent;
import com.teachsync.teachsyncevents.courses.CourseTeacherAssignedEvent;
import com.teachsync.teachsyncevents.courses.CourseTopicRemovedEvent;
import com.teachsync.teachsyncevents.courses.CourseTopicsAddedEvent;
import com.teachsync.teachsyncevents.courses.CourseUpdatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class CourseEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(
            CourseEventConsumer.class
    );

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    public CourseEventConsumer(NotificationService notificationService, ObjectMapper objectMapper) {
        this.notificationService = notificationService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(
            topics = KafkaTopics.COURSE_EVENTS,
            groupId = "notification-service",
            containerFactory = "kafkaListenerContainerFactory"
    )

    public void consumeCourseCreated(String rawMessage) {
        try {
            var node = objectMapper.readTree(rawMessage);
            String eventType = node.get("actionType").asText();
            switch (eventType) {
                case ActionTypes.COURSE_CREATED -> {
                    CourseCreatedEvent courseCreatedEvent = objectMapper.
                            readValue(rawMessage, CourseCreatedEvent.class);
                    handleCourseCreated(courseCreatedEvent);
                }
                case ActionTypes.COURSE_TEACHER_ASSIGNED -> {
                    CourseTeacherAssignedEvent courseTeacherAssignedEvent = objectMapper.
                            readValue(rawMessage, CourseTeacherAssignedEvent.class);
                    log.info("This message would be sent to teacher with: id {}, course name: {}",
                            courseTeacherAssignedEvent.getTeacherAssigned(),
                            courseTeacherAssignedEvent.getCourseName());
                }
                case ActionTypes.COURSE_EDITED -> {
                    CourseUpdatedEvent courseUpdatedEvent = objectMapper.
                            readValue(rawMessage, CourseUpdatedEvent.class);
                    log.info("CourseUpdatedEvent notification would be sent to all MANAGERS and ASSIGNED TEACHER, message: {}", courseUpdatedEvent.toString());
                }
                case ActionTypes.COURSE_GROUP_ENROLLED -> {
                    CourseGroupEnrolledEvent courseGroupEnrolledEvent = objectMapper.
                            readValue(rawMessage, CourseGroupEnrolledEvent.class);
                    log.info("CourseGroupEnrolledEvent notification would be sent to all MANAGERS and ASSIGNED TEACHER, data: {},{},{},{}",
                            courseGroupEnrolledEvent.getCourseId(), courseGroupEnrolledEvent.getGroupId(), courseGroupEnrolledEvent.getCourseName(), courseGroupEnrolledEvent.getGroupName()
                    );
                }
                case ActionTypes.COURSE_GROUP_REMOVED -> {
                    CourseGroupRelationRemovedEvent courseGroupRelationRemovedEvent = objectMapper.
                            readValue(rawMessage, CourseGroupRelationRemovedEvent.class);
                    log.info("CourseGroupRelationRemovedEvent notification would be sent ASSIGNED TEACHER and ALL MANAGERS(message content: need to create schedule for this course-group relation), data: course id: {},group id:{}, course name {},group name: {}, teacher id: {}",
                            courseGroupRelationRemovedEvent.getCourseId(),
                            courseGroupRelationRemovedEvent.getGroupId(),
                            courseGroupRelationRemovedEvent.getCourseName(),
                            courseGroupRelationRemovedEvent.getGroupName(),
                            courseGroupRelationRemovedEvent.getTeacherId()
                    );
                }
                case ActionTypes.COURSE_TOPIC_ADDED -> {
                    CourseTopicsAddedEvent courseTopicsAddedEvent = objectMapper.
                            readValue(rawMessage, CourseTopicsAddedEvent.class);
                    log.info("CourseTopicsAddedEvent notification would be send to ASSIGNED USER: {},{},{},{}",
                            courseTopicsAddedEvent.getCourseId(),
                            courseTopicsAddedEvent.getTopicId(),
                            courseTopicsAddedEvent.getCourseName(),
                            courseTopicsAddedEvent.getTopicName()
                    );
                }
                case ActionTypes.COURSE_TOPIC_REMOVED -> {
                    CourseTopicRemovedEvent courseTopicRemovedEvent = objectMapper.
                            readValue(rawMessage, CourseTopicRemovedEvent.class);
                    log.info("CourseTopicRemovedEvent notification would be send to ASSIGNED USER: {},{},{},{}",
                            courseTopicRemovedEvent.getCourseId(),
                            courseTopicRemovedEvent.getTopicId(),
                            courseTopicRemovedEvent.getCourseName(),
                            courseTopicRemovedEvent.getTopicName()
                    );
                }
            }
        } catch (Exception e) {
            log.error("Failed to process course event: {}", e.getMessage());
        }
    }

    private void handleCourseCreated(CourseCreatedEvent event) {
        Notification notification = new Notification();
        notification.setTargetSubject(TargetSubject.COURSE_CREATED);
        notification.setTargetRole(TargetRole.ADMIN);
        notification.setTitle("Создан новый курс");
        notification.setMessage("Курс " + event.getCourseName() + " был успешно создан");
        notification.setSourceService(event.getServiceName());
        notification.setEventId(event.getUuid());
        notificationService.save(notification);

        log.info("notification was saved for all ADMINS: {}", notification.getEventId());

    }
}
