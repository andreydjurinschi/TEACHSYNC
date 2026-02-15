package com.teachsync.interaction.clients;

import com.teachsync.interaction.requests.CourseBaseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
// todo consul discovery
@FeignClient(name = "course-service", /*path = "/internal/courses"*/ url = "http://localhost:8081/internal/courses")
public interface CourseClient {

    @GetMapping("/{id}")
    List<CourseBaseDto> requestForCourseInfo(@PathVariable("id") Long id);
}
