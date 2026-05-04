package com.teachsync.notificationservice.interaction.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teachsync.notificationservice.domain.TargetSubject;
import com.teachsync.notificationservice.enums.TargetRole;
import com.teachsync.notificationservice.service.NotificationService;
import com.teachsync.notificationservice.service.UserActivityService;
import com.teachsync.teachsyncevents.constants.ActionTypes;
import com.teachsync.teachsyncevents.constants.KafkaTopics;
import com.teachsync.teachsyncevents.courses.CourseCreatedEvent;
import com.teachsync.teachsyncevents.courses.CourseGroupEnrolledEvent;
import com.teachsync.teachsyncevents.courses.CourseGroupRelationRemovedEvent;
import com.teachsync.teachsyncevents.courses.CourseTeacherAssignedEvent;
import com.teachsync.teachsyncevents.courses.CourseTeacherUnassignedEvent;
import com.teachsync.teachsyncevents.courses.CourseTopicRemovedEvent;
import com.teachsync.teachsyncevents.courses.CourseTopicsAddedEvent;
import com.teachsync.teachsyncevents.courses.CourseUpdatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class CourseEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(CourseEventConsumer.class);

    private final NotificationService notificationService;
    private final UserActivityService activityService;
    private final ObjectMapper objectMapper;

    public CourseEventConsumer(NotificationService notificationService,
                               UserActivityService activityService,
                               ObjectMapper objectMapper) {
        this.notificationService = notificationService;
        this.activityService = activityService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(
            topics = KafkaTopics.COURSE_EVENTS,
            groupId = "notification-service",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeCourseEvents(String rawMessage) {
        try {
            var node = objectMapper.readTree(rawMessage);
            String eventType = node.get("actionType").asText();
            switch (eventType) {
                case ActionTypes.COURSE_CREATED -> handleCourseCreated(objectMapper.readValue(rawMessage, CourseCreatedEvent.class));
                case ActionTypes.COURSE_TEACHER_ASSIGNED -> handleCourseTeacherAssigned(objectMapper.readValue(rawMessage, CourseTeacherAssignedEvent.class));
                case ActionTypes.COURSE_TEACHER_UNASSIGNED -> handleCourseTeacherUnassigned(objectMapper.readValue(rawMessage, CourseTeacherUnassignedEvent.class));
                case ActionTypes.COURSE_EDITED -> handleCourseUpdated(objectMapper.readValue(rawMessage, CourseUpdatedEvent.class));
                case ActionTypes.COURSE_GROUP_ENROLLED -> handleCourseGroupEnrolled(objectMapper.readValue(rawMessage, CourseGroupEnrolledEvent.class));
                case ActionTypes.COURSE_GROUP_REMOVED -> handleCourseGroupRemoved(objectMapper.readValue(rawMessage, CourseGroupRelationRemovedEvent.class));
                case ActionTypes.COURSE_TOPIC_ADDED -> handleCourseTopicAdded(objectMapper.readValue(rawMessage, CourseTopicsAddedEvent.class));
                case ActionTypes.COURSE_TOPIC_REMOVED -> handleCourseTopicRemoved(objectMapper.readValue(rawMessage, CourseTopicRemovedEvent.class));
                default -> log.info("Unsupported course event type: {}", eventType);
            }
        } catch (Exception e) {
            log.error("Failed to process course event: {}", e.getMessage(), e);
        }
    }

    private void handleCourseCreated(CourseCreatedEvent event) {
        notificationService.saveForRole(
                event.getUuid(),
                event.getServiceName(),
                TargetSubject.COURSE_CREATED,
                TargetRole.ADMIN,
                "Создан новый курс",
                "Курс \"" + event.getCourseName() + "\" успешно создан."
        );
        activityService.recordForRole(
                event.getUuid(),
                event.getServiceName(),
                ActionTypes.COURSE_CREATED,
                TargetRole.ADMIN,
                null,
                "Course-service",
                "Создан курс",
                "Создан курс \"" + event.getCourseName() + "\".",
                event.getTeacherId() == null
                        ? "Курс создан без назначенного преподавателя."
                        : "Курс создан с преподавателем ID " + event.getTeacherId() + ".",
                "/courses/" + event.getCourseId()
        );
        log.info("Saved COURSE_CREATED notification for admins: {}", event.getUuid());
    }

    private void handleCourseTeacherAssigned(CourseTeacherAssignedEvent event) {
        notificationService.saveForUser(
                event.getUuid(),
                event.getServiceName(),
                TargetSubject.TEACHER_ASSIGNED,
                event.getTeacherAssigned(),
                "Назначение на курс",
                "Вы назначены преподавателем курса \"" + event.getCourseName() + "\"."
        );
        activityService.recordForUser(
                event.getUuid(),
                event.getServiceName(),
                ActionTypes.COURSE_TEACHER_ASSIGNED,
                event.getTeacherAssigned(),
                null,
                "Course-service",
                "Назначение на курс",
                "Вы назначены преподавателем курса \"" + event.getCourseName() + "\".",
                "Курс ID: " + event.getCourseId() + ".",
                "/courses/" + event.getCourseId()
        );
        log.info("Saved COURSE_TEACHER_ASSIGNED notification for teacher {}", event.getTeacherAssigned());
    }

    private void handleCourseTeacherUnassigned(CourseTeacherUnassignedEvent event) {
        String category = event.getCategoryName() == null ? "без указанной категории" : "категория: \"" + event.getCategoryName() + "\"";
        String message = "Курс \"" + event.getCourseName() + "\" остался без преподавателя (" + category + "). "
                + "Нужно назначить свободного преподавателя с подходящей категорией и согласовать ведение группы.";
        notificationService.saveForRole(
                event.getUuid(),
                event.getServiceName(),
                TargetSubject.COURSE_TEACHER_UNASSIGNED,
                TargetRole.MANAGER,
                "Курс без преподавателя",
                message
        );
        activityService.recordForRole(
                event.getUuid(),
                event.getServiceName(),
                ActionTypes.COURSE_TEACHER_UNASSIGNED,
                TargetRole.MANAGER,
                event.getPreviousTeacherId(),
                "Course-service",
                "Курс остался без преподавателя",
                "Курс \"" + event.getCourseName() + "\" остался без преподавателя.",
                "Категория: " + (event.getCategoryName() == null ? "не указана" : event.getCategoryName())
                        + ". Причина: " + (event.getReason() == null ? "не указана" : event.getReason()) + ".",
                "/courses/" + event.getCourseId()
        );
        log.info("Saved COURSE_TEACHER_UNASSIGNED notification for managers: course {}", event.getCourseId());
    }

    private void handleCourseUpdated(CourseUpdatedEvent event) {
        String courseName = event.getCourseName() == null || event.getCourseName().isBlank()
                ? "курс"
                : "\"" + event.getCourseName() + "\"";
        String message = "Курс " + courseName + " был обновлен.";
        String actionUrl = "/courses/" + event.getCourseId();
        notificationService.saveForRole(
                event.getUuid(),
                event.getServiceName(),
                TargetSubject.COURSE_UPDATED,
                TargetRole.MANAGER,
                "Курс обновлен",
                message,
                actionUrl
        );
        activityService.recordForRole(
                event.getUuid(),
                event.getServiceName(),
                ActionTypes.COURSE_EDITED,
                TargetRole.MANAGER,
                event.getChangedByUserId(),
                actorName(event),
                "Курс обновлен",
                "Курс " + courseName + " был обновлен пользователем " + actorName(event) + ".",
                "Было: " + event.getOldState() + "\nСтало: " + event.getNewState(),
                actionUrl
        );
        if (event.getChangedByUserId() != null) {
            activityService.recordForUser(
                    event.getUuid() + ":actor",
                    event.getServiceName(),
                    ActionTypes.COURSE_EDITED,
                    event.getChangedByUserId(),
                    event.getChangedByUserId(),
                    actorName(event),
                    "Вы обновили курс",
                    "Вы обновили курс " + courseName + ".",
                    "Было: " + event.getOldState() + "\nСтало: " + event.getNewState(),
                    actionUrl
            );
        }
        Long teacherId = extractTeacherId(event.getNewState());
        if (teacherId != null) {
            notificationService.saveForUser(
                    event.getUuid(),
                    event.getServiceName(),
                    TargetSubject.COURSE_UPDATED,
                    teacherId,
                    "Курс обновлен",
                    message,
                    actionUrl
            );
            activityService.recordForUser(
                    event.getUuid(),
                    event.getServiceName(),
                    ActionTypes.COURSE_EDITED,
                    teacherId,
                    event.getChangedByUserId(),
                    actorName(event),
                    "Курс обновлен",
                    "Курс " + courseName + " был обновлен пользователем " + actorName(event) + ".",
                    "Было: " + event.getOldState() + "\nСтало: " + event.getNewState(),
                    actionUrl
            );
        }
        log.info("Saved COURSE_EDITED notifications for managers and teacher {}", teacherId);
    }

    private void handleCourseGroupEnrolled(CourseGroupEnrolledEvent event) {
        String message = "Группа \"" + event.getGroupName() + "\" прикреплена к курсу \"" + event.getCourseName() + "\".";
        notificationService.saveForRole(
                event.getUuid(),
                event.getServiceName(),
                TargetSubject.COURSE_GROUP_ENROLLED,
                TargetRole.MANAGER,
                "Группа добавлена к курсу",
                message
        );
        activityService.recordForRole(
                event.getUuid(),
                event.getServiceName(),
                ActionTypes.COURSE_GROUP_ENROLLED,
                TargetRole.MANAGER,
                null,
                "Course-service",
                "Группа добавлена к курсу",
                message,
                "Курс ID: " + event.getCourseId() + ". Группа ID: " + event.getGroupId() + ".",
                "/courses/" + event.getCourseId()
        );
        if (event.getTeacherId() != null) {
            notificationService.saveForUser(
                    event.getUuid(),
                    event.getServiceName(),
                    TargetSubject.COURSE_GROUP_ENROLLED,
                    event.getTeacherId(),
                    "Группа добавлена к вашему курсу",
                    message
            );
            activityService.recordForUser(
                    event.getUuid(),
                    event.getServiceName(),
                    ActionTypes.COURSE_GROUP_ENROLLED,
                    event.getTeacherId(),
                    null,
                    "Course-service",
                    "Группа добавлена к вашему курсу",
                    message,
                    "Курс ID: " + event.getCourseId() + ". Группа ID: " + event.getGroupId() + ".",
                    "/courses/" + event.getCourseId()
            );
        }
        log.info("Saved COURSE_GROUP_ENROLLED notifications for course {}", event.getCourseId());
    }

    private void handleCourseGroupRemoved(CourseGroupRelationRemovedEvent event) {
        String message = "Связь курса \"" + event.getCourseName() + "\" и группы \"" + event.getGroupName() + "\" удалена. Требуется актуализация расписания.";
        notificationService.saveForRole(
                event.getUuid(),
                event.getServiceName(),
                TargetSubject.COURSE_GROUP_REMOVED,
                TargetRole.MANAGER,
                "Связь курса и группы удалена",
                message
        );
        activityService.recordForRole(
                event.getUuid(),
                event.getServiceName(),
                ActionTypes.COURSE_GROUP_REMOVED,
                TargetRole.MANAGER,
                null,
                "Course-service",
                "Связь курса и группы удалена",
                message,
                "Курс ID: " + event.getCourseId() + ". Группа ID: " + event.getGroupId() + ".",
                "/courses/" + event.getCourseId()
        );
        if (event.getTeacherId() != null) {
            notificationService.saveForUser(
                    event.getUuid(),
                    event.getServiceName(),
                    TargetSubject.COURSE_GROUP_REMOVED,
                    event.getTeacherId(),
                    "Изменение состава курса",
                    message
            );
            activityService.recordForUser(
                    event.getUuid(),
                    event.getServiceName(),
                    ActionTypes.COURSE_GROUP_REMOVED,
                    event.getTeacherId(),
                    null,
                    "Course-service",
                    "Изменение состава курса",
                    message,
                    "Курс ID: " + event.getCourseId() + ". Группа ID: " + event.getGroupId() + ".",
                    "/courses/" + event.getCourseId()
            );
        }
        log.info("Saved COURSE_GROUP_REMOVED notifications for course {}", event.getCourseId());
    }

    private void handleCourseTopicAdded(CourseTopicsAddedEvent event) {
        if (event.getTeacherId() == null) {
            log.info("Skipped COURSE_TOPIC_ADDED notification for course {} because teacher is not assigned", event.getCourseId());
            return;
        }
        notificationService.saveForUser(
                event.getUuid(),
                event.getServiceName(),
                TargetSubject.COURSE_TOPIC_ADDED,
                event.getTeacherId(),
                "Новая тема в курсе",
                "В курс \"" + event.getCourseName() + "\" добавлена тема \"" + event.getTopicName() + "\"."
        );
        activityService.recordForUser(
                event.getUuid(),
                event.getServiceName(),
                ActionTypes.COURSE_TOPIC_ADDED,
                event.getTeacherId(),
                null,
                "Course-service",
                "Добавлена тема курса",
                "В курс \"" + event.getCourseName() + "\" добавлена тема \"" + event.getTopicName() + "\".",
                "Курс ID: " + event.getCourseId() + ". Тема ID: " + event.getTopicId() + ".",
                "/courses/" + event.getCourseId()
        );
        log.info("Saved COURSE_TOPIC_ADDED notification for teacher {}", event.getTeacherId());
    }

    private void handleCourseTopicRemoved(CourseTopicRemovedEvent event) {
        if (event.getTeacherId() == null) {
            log.info("Skipped COURSE_TOPIC_REMOVED notification for course {} because teacher is not assigned", event.getCourseId());
            return;
        }
        notificationService.saveForUser(
                event.getUuid(),
                event.getServiceName(),
                TargetSubject.COURSE_TOPIC_REMOVED,
                event.getTeacherId(),
                "Тема удалена из курса",
                "Из курса \"" + event.getCourseName() + "\" удалена тема \"" + event.getTopicName() + "\"."
        );
        activityService.recordForUser(
                event.getUuid(),
                event.getServiceName(),
                ActionTypes.COURSE_TOPIC_REMOVED,
                event.getTeacherId(),
                null,
                "Course-service",
                "Удалена тема курса",
                "Из курса \"" + event.getCourseName() + "\" удалена тема \"" + event.getTopicName() + "\".",
                "Курс ID: " + event.getCourseId() + ". Тема ID: " + event.getTopicId() + ".",
                "/courses/" + event.getCourseId()
        );
        log.info("Saved COURSE_TOPIC_REMOVED notification for teacher {}", event.getTeacherId());
    }

    private Long extractTeacherId(String courseState) {
        if (courseState == null) {
            return null;
        }
        String marker = "teacherId=";
        int start = courseState.indexOf(marker);
        if (start < 0) {
            return null;
        }
        int valueStart = start + marker.length();
        int valueEnd = courseState.indexOf(',', valueStart);
        if (valueEnd < 0) {
            valueEnd = courseState.indexOf('}', valueStart);
        }
        if (valueEnd < 0) {
            valueEnd = courseState.length();
        }
        String teacherValue = courseState.substring(valueStart, valueEnd).trim();
        if (teacherValue.isBlank() || "null".equalsIgnoreCase(teacherValue)) {
            return null;
        }
        return Long.valueOf(teacherValue);
    }

    private String actorName(CourseUpdatedEvent event) {
        if (event.getChangedByName() == null || event.getChangedByName().isBlank()) {
            return "Course-service";
        }
        if (event.getChangedByRole() == null || event.getChangedByRole().isBlank()) {
            return event.getChangedByName();
        }
        return event.getChangedByName() + " (" + event.getChangedByRole() + ")";
    }
}
