package com.teachsync.notificationservice.dto;

import com.teachsync.notificationservice.domain.TargetSubject;
import com.teachsync.notificationservice.enums.TargetRole;

import java.time.LocalDateTime;

public class NotificationDto {

    private Long id;
    private String title;
    private String message;
    private TargetRole targetRole;
    private Long targetUserId;
    private TargetSubject targetSubject;
    private String sourceService;
    private boolean read;
    private LocalDateTime createdAt;

    public NotificationDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public TargetRole getTargetRole() { return targetRole; }
    public void setTargetRole(TargetRole targetRole) { this.targetRole = targetRole; }
    public Long getTargetUserId() { return targetUserId; }
    public void setTargetUserId(Long targetUserId) { this.targetUserId = targetUserId; }
    public TargetSubject getTargetSubject() { return targetSubject; }
    public void setTargetSubject(TargetSubject targetSubject) { this.targetSubject = targetSubject; }
    public String getSourceService() { return sourceService; }
    public void setSourceService(String sourceService) { this.sourceService = sourceService; }
    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}