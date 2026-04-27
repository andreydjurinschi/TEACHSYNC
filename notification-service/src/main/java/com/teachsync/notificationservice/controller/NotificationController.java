package com.teachsync.notificationservice.controller;

import com.teachsync.notificationservice.dto.NotificationDto;
import com.teachsync.notificationservice.enums.TargetRole;
import com.teachsync.notificationservice.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teachsync/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<List<NotificationDto>> getForRole(
            @PathVariable TargetRole role,
            @RequestParam Long userId) {
        return ResponseEntity.ok(notificationService.getForRole(role, userId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationDto>> getForUser(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getForUser(userId));
    }

    @GetMapping("/role/{role}/unread-count")
    public ResponseEntity<Long> unreadForRole(
            @PathVariable TargetRole role,
            @RequestParam Long userId) {
        return ResponseEntity.ok(notificationService.countUnreadForRole(role, userId));
    }

    @GetMapping("/user/{userId}/unread-count")
    public ResponseEntity<Long> unreadForUser(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.countUnreadForUser(userId));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long id,
            @RequestParam Long userId) {
        notificationService.markAsRead(id, userId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/role/{role}/read-all")
    public ResponseEntity<Void> markAllRead(
            @PathVariable TargetRole role,
            @RequestParam Long userId) {
        notificationService.markAllReadForRole(role, userId);
        return ResponseEntity.ok().build();
    }
}