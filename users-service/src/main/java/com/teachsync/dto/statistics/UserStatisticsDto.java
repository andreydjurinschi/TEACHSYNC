package com.teachsync.dto.statistics;

public record UserStatisticsDto(
        long totalUsers,
        long adminCount,
        long managerCount,
        long teacherCount
) {
}
