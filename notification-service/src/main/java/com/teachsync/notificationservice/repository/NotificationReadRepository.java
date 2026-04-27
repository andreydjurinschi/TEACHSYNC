package com.teachsync.notificationservice.repository;

import com.teachsync.notificationservice.domain.NotificationRead;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface NotificationReadRepository extends JpaRepository<NotificationRead, Long> {

    Optional<NotificationRead> findByNotificationIdAndUserId(Long notificationId, Long userId);

    Set<NotificationRead> findByUserId(Long userId);

    boolean existsByNotificationIdAndUserId(Long notificationId, Long userId);
}