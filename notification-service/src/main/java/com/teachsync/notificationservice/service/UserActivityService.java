package com.teachsync.notificationservice.service;

import com.teachsync.notificationservice.domain.UserActivity;
import com.teachsync.notificationservice.dto.UserActivityDto;
import com.teachsync.notificationservice.enums.TargetRole;
import com.teachsync.notificationservice.repository.UserActivityRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
public class UserActivityService {

    private static final int MAX_LIMIT = 50;

    private final UserActivityRepository repository;

    public UserActivityService(UserActivityRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void recordForUser(String eventId,
                              String sourceService,
                              String actionType,
                              Long targetUserId,
                              Long actorUserId,
                              String actorName,
                              String title,
                              String summary,
                              String details,
                              String actionUrl) {
        record(eventId, sourceService, actionType, targetUserId, null, actorUserId, actorName, title, summary, details, actionUrl);
    }

    @Transactional
    public void recordForRole(String eventId,
                              String sourceService,
                              String actionType,
                              TargetRole targetRole,
                              Long actorUserId,
                              String actorName,
                              String title,
                              String summary,
                              String details,
                              String actionUrl) {
        record(eventId, sourceService, actionType, null, targetRole, actorUserId, actorName, title, summary, details, actionUrl);
    }

    public List<UserActivityDto> getForUser(Long userId, TargetRole role, int limit) {
        int safeLimit = Math.max(1, Math.min(limit, MAX_LIMIT));
        PageRequest page = PageRequest.of(0, safeLimit);

        List<UserActivity> personal = repository.findByTargetUserIdOrderByCreatedAtDesc(userId, page);
        return personal.stream()
                .sorted(Comparator.comparing(UserActivity::getCreatedAt).reversed())
                .limit(safeLimit)
                .map(this::toDto)
                .toList();
    }

    private void record(String eventId,
                        String sourceService,
                        String actionType,
                        Long targetUserId,
                        TargetRole targetRole,
                        Long actorUserId,
                        String actorName,
                        String title,
                        String summary,
                        String details,
                        String actionUrl) {
        if (repository.existsByEventIdAndTargetUserIdAndTargetRole(eventId, targetUserId, targetRole)) {
            return;
        }

        UserActivity activity = new UserActivity();
        activity.setEventId(eventId);
        activity.setSourceService(sourceService);
        activity.setActionType(actionType);
        activity.setTargetUserId(targetUserId);
        activity.setTargetRole(targetRole);
        activity.setActorUserId(actorUserId);
        activity.setActorName(actorName);
        activity.setTitle(title);
        activity.setSummary(summary);
        activity.setDetails(details);
        activity.setActionUrl(actionUrl);
        repository.save(activity);
    }

    private UserActivityDto toDto(UserActivity activity) {
        UserActivityDto dto = new UserActivityDto();
        dto.setId(activity.getId());
        dto.setEventId(activity.getEventId());
        dto.setSourceService(activity.getSourceService());
        dto.setActionType(activity.getActionType());
        dto.setTargetUserId(activity.getTargetUserId());
        dto.setTargetRole(activity.getTargetRole());
        dto.setActorUserId(activity.getActorUserId());
        dto.setActorName(activity.getActorName());
        dto.setTitle(activity.getTitle());
        dto.setSummary(activity.getSummary());
        dto.setDetails(activity.getDetails());
        dto.setActionUrl(activity.getActionUrl());
        dto.setCreatedAt(activity.getCreatedAt());
        return dto;
    }
}
