package com.teachsync.dto_s.domain.statistics;

public record ScheduleStatisticsDto(
        long totalSchedules,
        long scheduledGroupCourses,
        long unscheduledGroupCourses,
        long totalGroupCourses,
        long classrooms
) {
}
