package com.teachsync.interaction.feign.clients;

import com.teachsync.dto_s.internal.ScheduleCleanupRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "schedule-cleanup-service",
        url = "${teachsync.services.schedules.url:http://localhost:8082/internal/schedules}"
)
public interface ScheduleCleanupClient {

    @PostMapping("/cleanup/group-courses")
    void cleanupSchedulesByGroupCourses(@RequestBody ScheduleCleanupRequest request);
}
