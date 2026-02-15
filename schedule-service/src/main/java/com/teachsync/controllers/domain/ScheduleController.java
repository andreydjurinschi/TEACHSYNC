package com.teachsync.controllers.domain;

import com.teachsync.dto_s.domain.schedule.ScheduleBaseDto;
import com.teachsync.services.ScheduleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
