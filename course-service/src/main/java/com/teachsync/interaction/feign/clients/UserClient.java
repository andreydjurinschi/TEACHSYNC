package com.teachsync.interaction.feign.clients;

import com.teachsync.interaction.feign.requests.TeacherCheckResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// todo consul discovery

@FeignClient(
        name = "user-service",
        url = "http://localhost:8080/internal/users"
)
public interface UserClient {

    @GetMapping("/{id}/teacher")
    TeacherCheckResponse isTeacher(@PathVariable Long id);
}
