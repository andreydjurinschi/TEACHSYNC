package com.teachsync.notificationservice.repository;

import com.teachsync.notificationservice.domain.UserActivity;
import com.teachsync.notificationservice.enums.TargetRole;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserActivityRepository extends JpaRepository<UserActivity, Long> {

    List<UserActivity> findByTargetUserIdOrderByCreatedAtDesc(Long targetUserId, Pageable pageable);

    List<UserActivity> findByTargetRoleOrderByCreatedAtDesc(TargetRole targetRole, Pageable pageable);

    boolean existsByEventIdAndTargetUserIdAndTargetRole(String eventId, Long targetUserId, TargetRole targetRole);
}
