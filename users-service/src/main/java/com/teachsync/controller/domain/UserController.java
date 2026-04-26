package com.teachsync.controller.domain;

import com.teachsync.domain.Role;
import com.teachsync.dto.AccountUpdateDto;
import com.teachsync.dto.UserBaseDto;
import com.teachsync.dto.UserCreateDto;
import com.teachsync.dto.UserUpdateDto;
import com.teachsync.dto.feign.UserWithCoursesDto;
import com.teachsync.interaction.responses.feign.SpecializationsBaseDto;
import com.teachsync.interaction.responses.feign.TeacherResponse;
import com.teachsync.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/teachsync/users")
public class UserController {
    private final UserService service;

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

    @PutMapping("/edit/account/{id}")
    public ResponseEntity<Void> editAccount(@PathVariable Long id, @Valid @RequestBody AccountUpdateDto dto){
        service.editUserAccount(id, dto);
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

    @GetMapping("/{id}/specializations")
    public ResponseEntity<Set<SpecializationsBaseDto>> getSpecializations(@PathVariable Long id){
        return ResponseEntity.ok(service.getSpecializations(id));
    }

    @PostMapping("/{teacherId}/specializations/{categoryId}")
    public ResponseEntity<Void> addSpecializations(@PathVariable Long teacherId, @PathVariable Long categoryId){
        service.addSpecializationForTeacher(teacherId, categoryId);
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @GetMapping("/teachers")
    public ResponseEntity<List<TeacherResponse>> getAllTeachers(){
        return ResponseEntity.ok(
                service.findAllByRole(Role.TEACHER).stream().map(
                        t -> new  TeacherResponse(
                                t.getId(), t.getName(), t.getSurname(), t.getEmail(), t.getSpecializations()
                        )
                ).collect(Collectors.toList())
        );
    }

    @DeleteMapping("/{teacherId}/specializations/{categoryId}")
    public ResponseEntity<Void> deleteSpecializations(@PathVariable Long teacherId, @PathVariable Long categoryId){
        service.removeSpecializationForTeacher(teacherId, categoryId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }
}
