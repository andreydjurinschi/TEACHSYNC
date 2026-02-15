package com.teachsync.interation.feign.clients;

import com.teachsync.interation.feign.requests.GroupCourseBaseInfoRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// todo consul discovery

@FeignClient(name = "course-service", url = "http://localhost:8081/internal/courses")
public interface GroupCourseClient {
    @GetMapping("/group/{groupCourseId}")
    GroupCourseBaseInfoRequest groupCourseBaseInfoRequest(@PathVariable("groupCourseId")Long groupCourseId);
}
