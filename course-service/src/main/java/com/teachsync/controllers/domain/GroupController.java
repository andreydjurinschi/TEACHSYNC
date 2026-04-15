package com.teachsync.controllers.domain;

import com.teachsync.dto_s.groups.GroupBaseDto;
import com.teachsync.dto_s.groups.GroupCreateDto;
import com.teachsync.dto_s.groups.GroupUpdateDto;
import com.teachsync.dto_s.groups.GroupWithCoursesDto;
import com.teachsync.services.domain.GroupService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teachsync/groups")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<GroupBaseDto>> getAll() {
        return ResponseEntity.status(HttpStatus.OK).body(groupService.getAll());
    }

    @GetMapping("/{id}/courses")
    public ResponseEntity<GroupWithCoursesDto> getWithCourses(@PathVariable("id") Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(groupService.getDetailedDto(id));
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<Void> updateGroup(@PathVariable Long id, @Valid @RequestBody GroupUpdateDto dto) {
        groupService.update(id, dto);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable("id") Long id) {
        groupService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @PostMapping("/create")
    public ResponseEntity<Void> create(@RequestBody @Valid GroupCreateDto dto) {
        groupService.create(dto);
        return ResponseEntity.ok().body(null);
    }


    @PostMapping("/assign-to-course/{groupId}/{courseId}")
    public ResponseEntity<Void> assignToCourse(@PathVariable("groupId") Long groupId, @PathVariable("courseId") Long courseId) {
        groupService.assignGroupToCourse(groupId, courseId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/unassign-from-course/{groupId}/{courseId}")
    public ResponseEntity<Void> unassignFromCourse(@PathVariable Long groupId, @PathVariable Long courseId) {
        groupService.unassignGroupFromCourse(groupId, courseId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupBaseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(groupService.getById(id));
    }


}
