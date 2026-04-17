package com.teachsync.controllers.domain;

import com.teachsync.dto_s.courses.CourseBaseDto;
import com.teachsync.dto_s.courses.CourseCreateDto;
import com.teachsync.dto_s.courses.CourseDetailedDto;
import com.teachsync.dto_s.courses.CourseUpdateDto;
import com.teachsync.dto_s.courses.CourseWithGroupDto;
import com.teachsync.dto_s.feign.CourseWithTeacherRequest;
import com.teachsync.services.domain.CourseService;
import feign.Response;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/teachsync/courses")
public class CourseController {

    private final CourseService courseService;

    @Autowired
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<CourseBaseDto>> findAll() {
        return ResponseEntity.status(HttpStatus.OK).body(courseService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseBaseDto> findById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(courseService.findById(id));
    }

    @GetMapping("/course-with-groups/{id}")
    public ResponseEntity<CourseWithGroupDto> findCourseWithGroup(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(courseService.getCourseWithGroup(id));
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @Valid @RequestBody CourseUpdateDto dto) {
        courseService.updateCourse(id, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @PostMapping("/create")
    public ResponseEntity<Void> create(@RequestBody @Valid CourseCreateDto dto) {
        courseService.createCourse(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @PostMapping("/assign-group/{course_id}/{group_id}")
    public ResponseEntity<Void> assignGroupToCourse(@PathVariable Long course_id, @PathVariable Long group_id) {
        courseService.assignGroupToCourse(course_id, group_id);
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(null);
    }

    @DeleteMapping("/unassign-group/{course_id}/{group_id}")
    public ResponseEntity<Void> unassignGroupToCourse(@PathVariable Long course_id, @PathVariable Long group_id) {
        courseService.unassignGroupToCourse(course_id, group_id);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(null);
    }

    @GetMapping("/{id}/info")
    public ResponseEntity<CourseDetailedDto> getCourseFullInfo(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK).body(courseService.getAllCourseData(id));
    }

    @PostMapping("/assign-topic/{courseId}/{topicId}")
    public ResponseEntity<Void> assignTopicToCourse(@PathVariable("courseId") Long courseId, @PathVariable("topicId") Long topicId) {
        courseService.assignTopicToCourse(courseId, topicId);
        return ResponseEntity
                .status(HttpStatus.CREATED).body(null);
    }

    @DeleteMapping("/unassign-topic/{courseId}/{topicId}")
    public ResponseEntity<Void> unassignTopicFromCourse(@PathVariable("courseId") Long courseId, @PathVariable("topicId") Long topicId){
        courseService.unassignTopicToCourse(courseId, topicId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT).body(null);
    }
    // feign
    @PutMapping("/assign/{courseId}/{teacherId}")
    public ResponseEntity<Void> isTeacher(@PathVariable("courseId") Long courseId, @PathVariable("teacherId") Long teacherId) {
        courseService.assignTeacherToCourse(courseId, teacherId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/with-teacher/{id}")
    public ResponseEntity<CourseWithTeacherRequest> getCourseWithTeacher(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseWithTeacher(id));
    }
    // kafka requests
}
