package com.teachsync.interaction.feign.clients.schedule;

import com.teachsync.interaction.requests.ScheduleBaseDtoRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "schedule-service",
        url = "http://localhost:8082/internal/schedules"
)
public interface ScheduleClient {

    @GetMapping("/{id}")
    ScheduleBaseDtoRequest getSchedule(@PathVariable Long id);
}
