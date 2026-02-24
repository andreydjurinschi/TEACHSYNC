package com.teachsync.controllers.internal;

import com.teachsync.dto_s.domain.schedule.ScheduleBaseDto;
import com.teachsync.services.ScheduleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/schedules")
public class ScheduleInternalController {

    private final ScheduleService scheduleService;

    public ScheduleInternalController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping("/{id}")
    public ScheduleBaseDto getSchedule(@PathVariable Long id){
        return scheduleService.getById(id);
    }
}
