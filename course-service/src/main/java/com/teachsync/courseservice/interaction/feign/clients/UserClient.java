package com.teachsync.courseservice.interaction.feign.clients;

import com.teachsync.courseservice.interaction.feign.requests.TeacherCheckResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "user-service",
        url = "http://localhost:8080/internal/teachsync/users"
)
public interface UserClient {

    @GetMapping("/{id}/teacher")
    TeacherCheckResponse isTeacher(@PathVariable Long id);
}
