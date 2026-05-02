package com.teachsync.notificationservice.service;

import com.teachsync.notificationservice.domain.Notification;
import com.teachsync.notificationservice.domain.NotificationPreference;
import com.teachsync.notificationservice.domain.TargetSubject;
import com.teachsync.notificationservice.dto.NotificationPreferenceDto;
import com.teachsync.notificationservice.repository.NotificationPreferenceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationPreferenceService {

    private final NotificationPreferenceRepository preferenceRepository;

    public NotificationPreferenceService(NotificationPreferenceRepository preferenceRepository) {
        this.preferenceRepository = preferenceRepository;
    }

    @Transactional
    public NotificationPreferenceDto getForUser(Long userId) {
        return toDto(getOrCreate(userId));
    }

    @Transactional
    public NotificationPreferenceDto update(Long userId, NotificationPreferenceDto dto) {
        NotificationPreference preference = getOrCreate(userId);
        preference.setScheduleEnabled(dto.isScheduleEnabled());
        preference.setReplacementEnabled(dto.isReplacementEnabled());
        preference.setCourseEnabled(dto.isCourseEnabled());
        preference.setSystemEnabled(dto.isSystemEnabled());
        preference.setRealtimeEnabled(dto.isRealtimeEnabled());
        preference.setImportantOnly(dto.isImportantOnly());
        return toDto(preference);
    }

    @Transactional
    public NotificationPreference getOrCreate(Long userId) {
        return preferenceRepository.findByUserId(userId)
                .orElseGet(() -> preferenceRepository.save(new NotificationPreference(userId)));
    }

    public boolean shouldStoreForUser(Long userId, TargetSubject subject) {
        return isAllowed(getOrCreate(userId), subject);
    }

    public boolean shouldShowToUser(Long userId, Notification notification) {
        return isAllowed(getOrCreate(userId), notification.getTargetSubject());
    }

    public boolean shouldPushToUser(Long userId, Notification notification) {
        NotificationPreference preference = getOrCreate(userId);
        return preference.isRealtimeEnabled() && isAllowed(preference, notification.getTargetSubject());
    }

    public boolean shouldPushToUser(Long userId, TargetSubject subject) {
        NotificationPreference preference = getOrCreate(userId);
        return preference.isRealtimeEnabled() && isAllowed(preference, subject);
    }

    private boolean isAllowed(NotificationPreference preference, TargetSubject subject) {
        if (subject == null) {
            return preference.isSystemEnabled() && !preference.isImportantOnly();
        }
        if (preference.isImportantOnly() && !isImportant(subject)) {
            return false;
        }
        return switch (categoryOf(subject)) {
            case SCHEDULE -> preference.isScheduleEnabled();
            case REPLACEMENT -> preference.isReplacementEnabled();
            case COURSE -> preference.isCourseEnabled();
            case SYSTEM -> preference.isSystemEnabled();
        };
    }

    private boolean isImportant(TargetSubject subject) {
        return switch (subject) {
            case REPLACEMENT_REQUESTED,
                 REPLACEMENT_APPROVED,
                 REPLACEMENT_STATUS_CHANGED,
                 COURSE_TEACHER_UNASSIGNED,
                 TEACHER_ASSIGNMENT_REQUESTED,
                 TEACHER_ASSIGNED,
                 SCHEDULE_CREATED,
                 SCHEDULE_UPDATED,
                 USER_ROLE_CHANGED -> true;
            default -> false;
        };
    }

    private NotificationCategory categoryOf(TargetSubject subject) {
        return switch (subject) {
            case SCHEDULE_CREATED,
                 SCHEDULE_UPDATED -> NotificationCategory.SCHEDULE;
            case REPLACEMENT_REQUESTED,
                 REPLACEMENT_APPROVED,
                 REPLACEMENT_STATUS_CHANGED -> NotificationCategory.REPLACEMENT;
            case COURSE_CREATED,
                 COURSE_UPDATED,
                 COURSE_GROUP_ENROLLED,
                 COURSE_GROUP_REMOVED,
                 COURSE_TOPIC_ADDED,
                 COURSE_TOPIC_REMOVED,
                 COURSE_TEACHER_UNASSIGNED,
                 TEACHER_ASSIGNMENT_REQUESTED,
                 TEACHER_ASSIGNED -> NotificationCategory.COURSE;
            case USER_CREATED,
                 USER_DELETED,
                 USER_ROLE_CHANGED,
                 USER_SPECIALIZATION_ADDED,
                 USER_SPECIALIZATION_REMOVED -> NotificationCategory.SYSTEM;
        };
    }

    private NotificationPreferenceDto toDto(NotificationPreference preference) {
        NotificationPreferenceDto dto = new NotificationPreferenceDto();
        dto.setUserId(preference.getUserId());
        dto.setScheduleEnabled(preference.isScheduleEnabled());
        dto.setReplacementEnabled(preference.isReplacementEnabled());
        dto.setCourseEnabled(preference.isCourseEnabled());
        dto.setSystemEnabled(preference.isSystemEnabled());
        dto.setRealtimeEnabled(preference.isRealtimeEnabled());
        dto.setImportantOnly(preference.isImportantOnly());
        return dto;
    }

    private enum NotificationCategory {
        SCHEDULE,
        REPLACEMENT,
        COURSE,
        SYSTEM
    }
}
