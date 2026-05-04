package com.teachsync.dto_s.statistics;

public record TeacherReplacementStatisticsDto(
        long requestedReplacements,
        long helpedReplacements,
        long pendingInvitations,
        long declinedInvitations
) {
}
