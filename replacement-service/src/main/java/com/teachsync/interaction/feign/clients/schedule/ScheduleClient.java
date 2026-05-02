package com.teachsync.interaction.feign.clients.schedule;

import com.teachsync.interaction.requests.ScheduleBaseDtoRequest;
import com.teachsync.interaction.requests.WeekDays;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(
        name = "schedule-service",
        url = "${teachsync.services.schedules.url:http://localhost:8082/internal/schedules}"
)
public interface ScheduleClient {

    @GetMapping("/{id}")
    ScheduleBaseDtoRequest getSchedule(@PathVariable Long id);

    @GetMapping("/{id}/available-teachers")
    List<Long> getAvailableTeachers(@PathVariable Long id, @RequestParam WeekDays weekDay);
}
