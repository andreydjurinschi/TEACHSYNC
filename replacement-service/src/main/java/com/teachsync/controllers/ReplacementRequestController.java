package com.teachsync.controllers;

import com.teachsync.dto_s.replacementRequest.ReplacementRequestBaseDto;
import com.teachsync.dto_s.replacementRequest.ReplacementRequestCreateDto;
import com.teachsync.service.ReplacementRequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/teachsync/replacements")
public class ReplacementRequestController {

    private final ReplacementRequestService service;

    public ReplacementRequestController(ReplacementRequestService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReplacementRequestBaseDto> getById(@PathVariable Long id){
        return ResponseEntity.ok(service.getRequestById(id));
    }

    @PostMapping
    public ResponseEntity<ReplacementRequestBaseDto> create(@RequestBody ReplacementRequestCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}/approve/{teacherId}")
    public ResponseEntity<ReplacementRequestBaseDto> approve(@PathVariable Long id, @PathVariable Long teacherId) {
        return ResponseEntity.ok(service.approve(id, teacherId));
    }

    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<ReplacementRequestBaseDto>> getForTeacher(@PathVariable Long teacherId) {
        return ResponseEntity.ok(service.getForTeacher(teacherId));
    }
}
