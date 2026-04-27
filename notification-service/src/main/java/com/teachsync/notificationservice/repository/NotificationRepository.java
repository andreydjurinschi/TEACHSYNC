package com.teachsync.notificationservice.repository;

import com.teachsync.notificationservice.domain.Notification;
import com.teachsync.notificationservice.enums.TargetRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByTargetUserIdOrderByCreatedAtDesc(Long userId);

    List<Notification> findByTargetRoleOrderByCreatedAtDesc(TargetRole role);
}