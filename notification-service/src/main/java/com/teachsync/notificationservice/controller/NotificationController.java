package com.teachsync.notificationservice.controller;

import com.teachsync.notificationservice.dto.NotificationDto;
import com.teachsync.notificationservice.dto.NotificationPreferenceDto;
import com.teachsync.notificationservice.dto.UserActivityDto;
import com.teachsync.notificationservice.enums.TargetRole;
import com.teachsync.notificationservice.service.NotificationPreferenceService;
import com.teachsync.notificationservice.service.NotificationService;
import com.teachsync.notificationservice.service.UserActivityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/teachsync/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationPreferenceService preferenceService;
    private final UserActivityService activityService;

    public NotificationController(NotificationService notificationService,
                                  NotificationPreferenceService preferenceService,
                                  UserActivityService activityService) {
        this.notificationService = notificationService;
        this.preferenceService = preferenceService;
        this.activityService = activityService;
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

    @GetMapping("/preferences/{userId}")
    public ResponseEntity<NotificationPreferenceDto> getPreferences(@PathVariable Long userId) {
        return ResponseEntity.ok(preferenceService.getForUser(userId));
    }

    @PutMapping("/preferences/{userId}")
    public ResponseEntity<NotificationPreferenceDto> updatePreferences(
            @PathVariable Long userId,
            @RequestBody NotificationPreferenceDto dto) {
        return ResponseEntity.ok(preferenceService.update(userId, dto));
    }

    @GetMapping("/stream")
    public SseEmitter stream(@RequestParam Long userId, @RequestParam TargetRole role) {
        return notificationService.subscribe(userId, role);
    }

    @GetMapping("/activities/user/{userId}")
    public ResponseEntity<List<UserActivityDto>> getUserActivities(
            @PathVariable Long userId,
            @RequestParam(required = false) TargetRole role,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(activityService.getForUser(userId, role, limit));
    }
}
