package com.teachsync.controllers.internal;

import com.teachsync.dto_s.domain.schedule.ScheduleBaseDto;
import com.teachsync.domain.WeekDays;
import com.teachsync.services.ScheduleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @GetMapping("/{id}/available-teachers")
    public List<Long> availableTeachers(@PathVariable Long id, @RequestParam WeekDays weekDay) {
        return scheduleService.findAvailableTeachers(id, weekDay);
    }
}
