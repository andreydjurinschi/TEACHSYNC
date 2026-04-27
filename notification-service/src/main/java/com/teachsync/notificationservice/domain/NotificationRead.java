package com.teachsync.notificationservice.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification_reads",
        uniqueConstraints = @UniqueConstraint(columnNames = {"notification_id", "user_id"}))
public class NotificationRead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "notification_id", nullable = false)
    private Long notificationId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "read_at")
    private LocalDateTime readAt = LocalDateTime.now();

    public NotificationRead() {}

    public NotificationRead(Long notificationId, Long userId) {
        this.notificationId = notificationId;
        this.userId = userId;
    }

    public Long getId() { return id; }
    public Long getNotificationId() { return notificationId; }
    public Long getUserId() { return userId; }
    public LocalDateTime getReadAt() { return readAt; }
    public void setId(Long id) { this.id = id; }
    public void setNotificationId(Long notificationId) { this.notificationId = notificationId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setReadAt(LocalDateTime readAt) { this.readAt = readAt; }
}