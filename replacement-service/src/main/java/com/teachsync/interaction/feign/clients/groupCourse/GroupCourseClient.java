package com.teachsync.interaction.feign.clients.groupCourse;

import com.teachsync.interaction.requests.nested.GroupCourseBaseInfoRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "course-service", url = "http://localhost:8081/internal/courses")
public interface GroupCourseClient {
    @GetMapping("/group/{groupCourseId}")
    GroupCourseBaseInfoRequest groupCourseBaseInfoRequest(@PathVariable("groupCourseId")Long groupCourseId);
}
