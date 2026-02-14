package com.teachsync.courseservice.controllers.domain;

import com.teachsync.courseservice.dto_s.groups.GroupBaseDto;
import com.teachsync.courseservice.dto_s.groups.GroupCreateDto;
import com.teachsync.courseservice.dto_s.groups.GroupUpdateDto;
import com.teachsync.courseservice.dto_s.groups.GroupWithCoursesDto;
import com.teachsync.courseservice.services.domain.GroupService;
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
    public ResponseEntity<List<GroupBaseDto>> getAll(){
        return ResponseEntity.status(HttpStatus.OK).body(groupService.getAll());
    }

    @GetMapping("/{id}/courses")
    public ResponseEntity<GroupWithCoursesDto> getWithCourses(@PathVariable("id") Long id){
        return ResponseEntity.status(HttpStatus.OK).body(groupService.getDetailedDto(id));
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<String> updateGroup(@PathVariable("id") Long id, @Valid @RequestBody GroupUpdateDto dto){
        groupService.update(id, dto);
        return ResponseEntity.status(HttpStatus.OK).body("Group was successfully updated.");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteGroup(@PathVariable("id") Long id){
        groupService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body("Group was successfully deleted.");
    }

    @PostMapping("/create")
    public ResponseEntity<String> create(@RequestBody @Valid GroupCreateDto dto){
        groupService.create(dto);
        return ResponseEntity.ok(String.format("Group %s was successfully created. Open date is %s", dto.getName(), dto.getOpenDate()));
    }


    @PostMapping("/assign-to-course/{groupId}/{courseId}")
    public ResponseEntity<Void> assignToCourse(@PathVariable("groupId") Long groupId, @PathVariable("courseId") Long courseId) {
        groupService.assignGroupToCourse(groupId, courseId);
        return ResponseEntity.ok().build();
    }

}
