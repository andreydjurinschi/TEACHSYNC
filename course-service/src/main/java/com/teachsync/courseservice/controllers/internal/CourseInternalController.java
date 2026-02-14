package com.teachsync.courseservice.controllers.internal;

import com.teachsync.courseservice.dto_s.courses.CourseBaseDto;
import com.teachsync.courseservice.interaction.feign.responses.GroupCourseResponseForScheduleService;
import com.teachsync.courseservice.services.domain.CourseService;
import com.teachsync.courseservice.services.feign.CourseFeignResponseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.util.List;

@RestController
@RequestMapping("/internal/teachsync/course")
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

    @GetMapping("/group/{groupCourseId}")
    public ResponseEntity<GroupCourseResponseForScheduleService>
           getGroupWithCourseForScheduleService(@PathVariable("groupCourseId")Long groupCourseId){
        return ResponseEntity.ok(responseService.getGroupCourse(groupCourseId));
    }
}
