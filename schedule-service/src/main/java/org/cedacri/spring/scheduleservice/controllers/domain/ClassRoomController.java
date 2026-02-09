package org.cedacri.spring.scheduleservice.controllers.domain;

import jakarta.validation.Valid;
import org.cedacri.spring.scheduleservice.dto_s.domain.class_room.ClassRoomBaseDto;
import org.cedacri.spring.scheduleservice.dto_s.domain.class_room.ClassRoomCreateDto;
import org.cedacri.spring.scheduleservice.dto_s.domain.class_room.ClassRoomDetailedDto;
import org.cedacri.spring.scheduleservice.services.ClassRoomService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
    public ResponseEntity<String> createClassRoom(@RequestBody @Valid ClassRoomCreateDto classRoomCreateDto){
        classRoomService.create(classRoomCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
