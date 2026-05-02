package com.teachsync.interaction.feign.clients;

import com.teachsync.interaction.feign.fallbacks.UserClientFallback;
import com.teachsync.interaction.feign.requests.TeacherCheckRequest;
import com.teachsync.interaction.feign.requests.TeacherRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//todo: service discovery

/**
 * feign клиент
 */
@FeignClient(
        name = "user-service",
        url = "${teachsync.services.users.url:http://localhost:8083/internal/users}",
        fallback = UserClientFallback.class
)
public interface UserClient {

    @GetMapping("/{id}/teacher")
    TeacherCheckRequest isTeacher(@PathVariable Long id);

    @GetMapping("/course_service/{id}")
    TeacherRequest getTeacher(@PathVariable Long id);

    @PostMapping("/batch")                              // ← POST, путь только /batch
    List<TeacherRequest> getTeachersByIds(@RequestBody List<Long> ids); // ← @RequestBody
}
