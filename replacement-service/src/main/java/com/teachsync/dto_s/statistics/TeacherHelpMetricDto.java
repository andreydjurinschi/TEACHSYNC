package com.teachsync.dto_s.statistics;

public record TeacherHelpMetricDto(
        Long teacherId,
        String teacherName,
        long approvedReplacements,
        long declinedReplacements,
        long pendingInvitations
) {
}
