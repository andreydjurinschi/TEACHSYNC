package com.teachsync.interaction.feign.clients.users;

import com.teachsync.interaction.requests.nested.TeacherBaseInfoRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "user-service",
        url = "http://localhost:8080/internal/users"
)
public interface UserClient {
    @GetMapping("/{id}")
    TeacherBaseInfoRequest getTeacher(@PathVariable Long id);
}
