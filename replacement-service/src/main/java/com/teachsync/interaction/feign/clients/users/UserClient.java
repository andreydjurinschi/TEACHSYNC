package com.teachsync.interaction.feign.clients.users;

import com.teachsync.interaction.requests.nested.TeacherBaseInfoRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(
        name = "user-service",
        url = "http://localhost:8083/internal/users"
)
public interface UserClient {
    @GetMapping("/{id}")
    TeacherBaseInfoRequest getTeacher(@PathVariable Long id);

    @GetMapping("/all/by-role")
    List<TeacherBaseInfoRequest> getAllByRole(@RequestParam String role);
}
