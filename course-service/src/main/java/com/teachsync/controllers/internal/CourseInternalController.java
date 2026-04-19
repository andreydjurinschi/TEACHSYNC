package com.teachsync.controllers.internal;

import com.teachsync.dto_s.courses.CourseBaseDto;
import com.teachsync.interaction.feign.responses.GroupCourseResponseForScheduleService;
import com.teachsync.services.domain.CourseService;
import com.teachsync.services.feign.CourseFeignResponseService;
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
@RequestMapping("/internal/courses")
public class CourseInternalController {

    private final CourseService courseService;
    private final CourseFeignResponseService responseService;

    public CourseInternalController(CourseService courseService, CourseFeignResponseService responseService) {
        this.courseService = courseService;
        this.responseService = responseService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<CourseBaseDto>> getCourseForUser(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(courseService.getAllForUser(id));
    }

    @GetMapping("/all")
    public List<GroupCourseResponseForScheduleService> getAll() {
        return responseService.getAll(); // добавь метод в сервис
    }

    @GetMapping("/group/{groupCourseId}")
    public ResponseEntity<GroupCourseResponseForScheduleService>
           getGroupWithCourseForScheduleService(@PathVariable("groupCourseId")Long groupCourseId){
        return ResponseEntity.ok(responseService.getGroupCourse(groupCourseId));
    }

    @PostMapping("/batch")
    public List<GroupCourseResponseForScheduleService> getByIds(@RequestBody List<Long> ids) {
        return responseService.findAllByIds(ids);
    }
}
