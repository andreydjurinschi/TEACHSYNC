package com.teachsync.notificationservice.domain;

import com.teachsync.notificationservice.enums.TargetRole;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "target_user_id")
    private Long targetUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_role")
    private TargetRole targetRole;
    @Enumerated(EnumType.STRING)
    @Column(name = "target_subj")
    private TargetSubject targetSubject;

    private String title;

    @Column(length = 1000)
    private String message;

    private String sourceService;
    private String eventId;


    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public Notification() {
    }

    public Long getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(Long targetUserId) {
        this.targetUserId = targetUserId;
    }

    public TargetRole getTargetRole() {
        return targetRole;
    }

    public void setTargetRole(TargetRole targetRole) {
        this.targetRole = targetRole;
    }

    public TargetSubject getTargetSubject() {
        return targetSubject;
    }

    public void setTargetSubject(TargetSubject targetSubject) {
        this.targetSubject = targetSubject;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSourceService() {
        return sourceService;
    }

    public void setSourceService(String sourceService) {
        this.sourceService = sourceService;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
