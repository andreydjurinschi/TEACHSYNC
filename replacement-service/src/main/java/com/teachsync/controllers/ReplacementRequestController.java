package com.teachsync.controllers;

import com.teachsync.dto_s.replacementRequest.ReplacementRequestBaseDto;
import com.teachsync.service.ReplacementRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
