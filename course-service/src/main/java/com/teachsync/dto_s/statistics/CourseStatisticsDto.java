package com.teachsync.dto_s.statistics;

public record CourseStatisticsDto(
        long totalCourses,
        long coursesWithTeacher,
        long coursesWithoutTeacher,
        long groupCourseRelations
) {
}
