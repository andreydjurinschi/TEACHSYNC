package com.teachsync.interaction.feign.clients;

import com.teachsync.interaction.feign.fallbacks.UserClientFallback;
import com.teachsync.interaction.feign.requests.TeacherCheckRequest;
import com.teachsync.interaction.feign.requests.TeacherRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// todo consul discovery

@FeignClient(
        name = "user-service",
        url = "http://localhost:8080/internal/users",
        fallback = UserClientFallback.class
)
public interface UserClient {

    @GetMapping("/{id}/teacher")
    TeacherCheckRequest isTeacher(@PathVariable Long id);

    @GetMapping("/course_service/{id}")
    TeacherRequest getTeacher(@PathVariable Long id);
}
