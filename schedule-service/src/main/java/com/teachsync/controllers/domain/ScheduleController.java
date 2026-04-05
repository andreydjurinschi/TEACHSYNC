package com.teachsync.controllers.domain;

import com.teachsync.domain.WeekDays;
import com.teachsync.dto_s.domain.schedule.ScheduleBaseDto;
import com.teachsync.services.ScheduleService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/teachsync/schedules")
public class ScheduleController {

    private final ScheduleService service;

    public ScheduleController(ScheduleService service) {
        this.service = service;
    }

    @GetMapping("/all")
    public ResponseEntity<List<ScheduleBaseDto>> getAll(){
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/available-teachers/{id}")
    public List<Long> availableTeachers(@PathVariable Long id,@RequestParam WeekDays weekDay) {
        return service.findAvailableTeachers(id, weekDay);
    }
}
