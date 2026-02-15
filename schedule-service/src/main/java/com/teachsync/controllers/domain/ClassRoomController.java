package com.teachsync.controllers.domain;

import jakarta.validation.Valid;
import com.teachsync.dto_s.domain.class_room.ClassRoomBaseDto;
import com.teachsync.dto_s.domain.class_room.ClassRoomCreateUpdateDto;
import com.teachsync.dto_s.domain.class_room.ClassRoomDetailedDto;
import com.teachsync.services.ClassRoomService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/teachsync/classrooms")
public class ClassRoomController {
    private final ClassRoomService classRoomService;

    public ClassRoomController(ClassRoomService classRoomService) {
        this.classRoomService = classRoomService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<ClassRoomBaseDto>> getAllClassRooms() {
        return ResponseEntity.ok(classRoomService.getAll());
    }

    @GetMapping("/detailed/{id}")
    public ResponseEntity<ClassRoomDetailedDto> getClassRoomWithSchedules(@PathVariable Long id) {
        return ResponseEntity.ok(classRoomService.getWithSchedules(id));
    }

    @PostMapping
    public ResponseEntity<String> createClassRoom(@RequestBody @Valid ClassRoomCreateUpdateDto classRoomCreateUpdateDto){
        classRoomService.create(classRoomCreateUpdateDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateClassRoom(@RequestBody @Valid ClassRoomCreateUpdateDto classRoomCreateUpdateDto, @PathVariable Long id){
        classRoomService.update(id, classRoomCreateUpdateDto);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteClassRoom(@PathVariable Long id){
        classRoomService.deleteClassRoom(id);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}
