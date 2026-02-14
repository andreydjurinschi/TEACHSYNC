package com.teachsync.controller.domain;

import com.teachsync.dto.UserBaseDto;
import com.teachsync.dto.UserCreateDto;
import com.teachsync.dto.UserUpdateDto;
import com.teachsync.dto.feign.UserWithCoursesDto;
import com.teachsync.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
    @RequestMapping("/teachsync/users")
public class UserController {
    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserBaseDto>> getAll(){
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserBaseDto> getById(@PathVariable Long id){
        return ResponseEntity.ok(service.findById(id));
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @Valid @RequestBody UserUpdateDto dto){
        service.updateUser(id, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @PostMapping("/create")
    public ResponseEntity<Void> create(@Valid @RequestBody UserCreateDto dto){
        service.createUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        service.deleteUser(id);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(null);
    }

    // feign

    @GetMapping("/teacher/{id}/courses")
    public ResponseEntity<UserWithCoursesDto> getWithCourses(@PathVariable Long id){
        return ResponseEntity.ok(service.getUserWithCourses(id));
    }
}
