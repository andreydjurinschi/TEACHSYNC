package com.teachsync.notificationservice.config;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class NotificationSchemaSynchronizer {

    @Bean
    ApplicationRunner synchronizeNotificationConstraints(JdbcTemplate jdbcTemplate) {
        return args -> {
            jdbcTemplate.execute("""
                    alter table notifications
                    drop constraint if exists notifications_target_subj_check
                    """);

            jdbcTemplate.execute("""
                    alter table notifications
                    add constraint notifications_target_subj_check
                    check (target_subj in (
                        'COURSE_CREATED',
                        'COURSE_DELETED',
                        'GROUP_DELETED',
                        'COURSE_UPDATED',
                        'COURSE_GROUP_ENROLLED',
                        'COURSE_GROUP_REMOVED',
                        'COURSE_TOPIC_ADDED',
                        'COURSE_TOPIC_REMOVED',
                        'COURSE_TEACHER_UNASSIGNED',
                        'TEACHER_ASSIGNMENT_REQUESTED',
                        'TEACHER_ASSIGNED',
                        'SCHEDULE_CREATED',
                        'SCHEDULE_UPDATED',
                        'SCHEDULE_DELETED',
                        'USER_CREATED',
                        'USER_DELETED',
                        'USER_ROLE_CHANGED',
                        'USER_SPECIALIZATION_ADDED',
                        'USER_SPECIALIZATION_REMOVED',
                        'REPLACEMENT_REQUESTED',
                        'REPLACEMENT_APPROVED',
                        'REPLACEMENT_STATUS_CHANGED',
                        'SYSTEM_ALERT'
                    ))
                    """);
        };
    }
}
