package com.teachsync.notificationservice.service;

import com.teachsync.notificationservice.domain.Notification;
import com.teachsync.notificationservice.domain.NotificationRead;
import com.teachsync.notificationservice.domain.TargetSubject;
import com.teachsync.notificationservice.dto.NotificationDto;
import com.teachsync.notificationservice.enums.TargetRole;
import com.teachsync.notificationservice.repository.NotificationReadRepository;
import com.teachsync.notificationservice.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationReadRepository readRepository;
    private final NotificationPreferenceService preferenceService;
    private final NotificationSseService sseService;

    public NotificationService(NotificationRepository notificationRepository,
                               NotificationReadRepository readRepository,
                               NotificationPreferenceService preferenceService,
                               NotificationSseService sseService) {
        this.notificationRepository = notificationRepository;
        this.readRepository = readRepository;
        this.preferenceService = preferenceService;
        this.sseService = sseService;
    }

    public void save(Notification notification) {
        notificationRepository.save(notification);
    }

    public SseEmitter subscribe(Long userId, TargetRole role) {
        preferenceService.getForUser(userId);
        return sseService.subscribe(userId, role);
    }

    @Transactional
    public void saveForRole(String eventId,
                            String sourceService,
                            TargetSubject targetSubject,
                            TargetRole targetRole,
                            String title,
                            String message) {
        saveForRole(eventId, sourceService, targetSubject, targetRole, title, message, null);
    }

    @Transactional
    public void saveForRole(String eventId,
                            String sourceService,
                            TargetSubject targetSubject,
                            TargetRole targetRole,
                            String title,
                            String message,
                            String actionUrl) {
        if (notificationRepository.existsByEventIdAndTargetRoleAndTargetUserId(eventId, targetRole, null)) {
            return;
        }

        Notification notification = new Notification();
        notification.setEventId(eventId);
        notification.setSourceService(sourceService);
        notification.setTargetSubject(targetSubject);
        notification.setTargetRole(targetRole);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setActionUrl(actionUrl);
        Notification saved = notificationRepository.save(notification);
        sseService.sendToRole(targetRole, toDto(saved, false), preferenceService);
    }

    @Transactional
    public void saveForUser(String eventId,
                            String sourceService,
                            TargetSubject targetSubject,
                            Long userId,
                            String title,
                            String message) {
        saveForUser(eventId, sourceService, targetSubject, userId, title, message, null);
    }

    @Transactional
    public void saveForUser(String eventId,
                            String sourceService,
                            TargetSubject targetSubject,
                            Long userId,
                            String title,
                            String message,
                            String actionUrl) {
        if (notificationRepository.existsByEventIdAndTargetRoleAndTargetUserId(eventId, null, userId)) {
            return;
        }

        Notification notification = new Notification();
        notification.setEventId(eventId);
        notification.setSourceService(sourceService);
        notification.setTargetSubject(targetSubject);
        notification.setTargetUserId(userId);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setActionUrl(actionUrl);
        Notification saved = notificationRepository.save(notification);
        if (preferenceService.shouldPushToUser(userId, saved)) {
            sseService.sendToUser(userId, toDto(saved, false));
        }
    }

    public List<NotificationDto> getForRole(TargetRole role, Long userId) {
        List<Notification> notifications =
                notificationRepository.findByTargetRoleOrderByCreatedAtDesc(role);

        Set<Long> readIds = readRepository.findByUserId(userId)
                .stream()
                .map(NotificationRead::getNotificationId)
                .collect(Collectors.toSet());

        return notifications.stream()
                .filter(n -> preferenceService.shouldShowToUser(userId, n))
                .map(n -> toDto(n, readIds.contains(n.getId())))
                .toList();
    }

    public List<NotificationDto> getForUser(Long userId) {
        List<Notification> notifications =
                notificationRepository.findByTargetUserIdOrderByCreatedAtDesc(userId);

        Set<Long> readIds = readRepository.findByUserId(userId)
                .stream()
                .map(NotificationRead::getNotificationId)
                .collect(Collectors.toSet());

        return notifications.stream()
                .filter(n -> preferenceService.shouldShowToUser(userId, n))
                .map(n -> toDto(n, readIds.contains(n.getId())))
                .toList();
    }

    public long countUnreadForRole(TargetRole role, Long userId) {
        return getForRole(role, userId).stream()
                .filter(n -> !n.isRead())
                .count();
    }

    public long countUnreadForUser(Long userId) {
        return getForUser(userId).stream()
                .filter(n -> !n.isRead())
                .count();
    }

    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        if (!readRepository.existsByNotificationIdAndUserId(notificationId, userId)) {
            readRepository.save(new NotificationRead(notificationId, userId));
        }
    }

    @Transactional
    public void markAllReadForRole(TargetRole role, Long userId) {
        List<Notification> all =
                notificationRepository.findByTargetRoleOrderByCreatedAtDesc(role);

        all.forEach(n -> {
            if (!readRepository.existsByNotificationIdAndUserId(n.getId(), userId)) {
                readRepository.save(new NotificationRead(n.getId(), userId));
            }
        });
    }

    private NotificationDto toDto(Notification n, boolean isRead) {
        NotificationDto dto = new NotificationDto();
        dto.setId(n.getId());
        dto.setTitle(n.getTitle());
        dto.setMessage(n.getMessage());
        dto.setTargetRole(n.getTargetRole());
        dto.setTargetUserId(n.getTargetUserId());
        dto.setTargetSubject(n.getTargetSubject());
        dto.setSourceService(n.getSourceService());
        dto.setActionUrl(n.getActionUrl());
        dto.setCreatedAt(n.getCreatedAt());
        dto.setRead(isRead);
        return dto;
    }
}
