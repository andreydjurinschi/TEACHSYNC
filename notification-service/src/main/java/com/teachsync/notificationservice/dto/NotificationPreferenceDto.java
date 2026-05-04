package com.teachsync.notificationservice.dto;

public class NotificationPreferenceDto {

    private Long userId;
    private boolean scheduleEnabled;
    private boolean replacementEnabled;
    private boolean courseEnabled;
    private boolean systemEnabled;
    private boolean realtimeEnabled;
    private boolean importantOnly;

    public NotificationPreferenceDto() {
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
