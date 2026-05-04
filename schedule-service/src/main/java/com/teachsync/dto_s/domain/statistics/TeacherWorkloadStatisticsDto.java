package com.teachsync.dto_s.domain.statistics;

public record TeacherWorkloadStatisticsDto(
        long weeklyLessons,
        long weeklyHours,
        long scheduledCourses,
        long scheduledGroups
) {
}
