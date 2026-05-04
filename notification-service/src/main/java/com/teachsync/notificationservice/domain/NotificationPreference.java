package com.teachsync.notificationservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "notification_preferences")
public class NotificationPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "schedule_enabled", nullable = false)
    private boolean scheduleEnabled = true;

    @Column(name = "replacement_enabled", nullable = false)
    private boolean replacementEnabled = true;

    @Column(name = "course_enabled", nullable = false)
    private boolean courseEnabled = true;

    @Column(name = "system_enabled", nullable = false)
    private boolean systemEnabled = true;

    @Column(name = "realtime_enabled", nullable = false)
    private boolean realtimeEnabled = true;

    @Column(name = "important_only", nullable = false)
    private boolean importantOnly = false;

    public NotificationPreference() {
    }

    public NotificationPreference(Long userId) {
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public boolean isScheduleEnabled() {
        return scheduleEnabled;
    }

    public void setScheduleEnabled(boolean scheduleEnabled) {
        this.scheduleEnabled = scheduleEnabled;
    }

    public boolean isReplacementEnabled() {
        return replacementEnabled;
    }

    public void setReplacementEnabled(boolean replacementEnabled) {
        this.replacementEnabled = replacementEnabled;
    }

    public boolean isCourseEnabled() {
        return courseEnabled;
    }

    public void setCourseEnabled(boolean courseEnabled) {
        this.courseEnabled = courseEnabled;
    }

    public boolean isSystemEnabled() {
        return systemEnabled;
    }

    public void setSystemEnabled(boolean systemEnabled) {
        this.systemEnabled = systemEnabled;
    }

    public boolean isRealtimeEnabled() {
        return realtimeEnabled;
    }

    public void setRealtimeEnabled(boolean realtimeEnabled) {
        this.realtimeEnabled = realtimeEnabled;
    }

    public boolean isImportantOnly() {
        return importantOnly;
    }

    public void setImportantOnly(boolean importantOnly) {
        this.importantOnly = importantOnly;
    }
}
