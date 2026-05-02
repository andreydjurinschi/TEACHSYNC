package com.teachsync.dto_s.statistics;

import java.util.List;

public record ReplacementStatisticsDto(
        long totalRequests,
        long pendingRequests,
        long approvedRequests,
        long declinedRequests,
        long expiredRequests,
        long cancelledRequests,
        long autoClosedRequests,
        List<TeacherHelpMetricDto> topHelpers
) {
}
