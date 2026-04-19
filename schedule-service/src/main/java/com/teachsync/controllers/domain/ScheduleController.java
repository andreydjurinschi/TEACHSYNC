package com.teachsync.controllers.domain;

import com.teachsync.domain.WeekDays;
import com.teachsync.dto_s.domain.class_room.ClassRoomBaseDto;
import com.teachsync.dto_s.domain.schedule.ScheduleBaseDto;
import com.teachsync.dto_s.domain.schedule.ScheduleCreateDto;
import com.teachsync.exceptions.InvalidTimeRangeException;
import com.teachsync.interation.feign.Role;
import com.teachsync.interation.feign.requests.GroupCourseBaseInfoRequest;
import com.teachsync.interation.feign.requests.TeacherBaseInfoRequest;
import com.teachsync.services.ScheduleService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
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

    @GetMapping("/teachers/all")
    public ResponseEntity<List<TeacherBaseInfoRequest>> getAllTeachers() {
        return ResponseEntity.ok(service.getAllTeachers(Role.TEACHER));
    }

    @GetMapping("/group-courses/all")
    public ResponseEntity<List<GroupCourseBaseInfoRequest>> getAllGroupCourses() {
        return ResponseEntity.ok(service.getAllGroupCourses());
    }

    @GetMapping("/classrooms/all")
    public ResponseEntity<List<ClassRoomBaseDto>> getAllClassrooms() {
        return ResponseEntity.ok(service.getAllClassrooms());
    }

    @GetMapping("/all")
    public ResponseEntity<List<ScheduleBaseDto>> getAll(){
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/available-teachers/{id}")
    public List<Long> availableTeachers(@PathVariable Long id,@RequestParam WeekDays weekDay) {
        return service.findAvailableTeachers(id, weekDay);
    }

    @PostMapping("/create")
    public ResponseEntity<Void> createSchedule(@RequestBody ScheduleCreateDto dto) throws InvalidTimeRangeException {
        service.create(dto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
