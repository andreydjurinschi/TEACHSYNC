package com.teachsync.interation.feign.clients;

import com.teachsync.interation.feign.requests.TeacherBaseInfoRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// todo consul discovery

@FeignClient(name = "users-service", url = "http://localhost:8080/internal/users")
public interface TeacherClient {
    @GetMapping("/{id}")
    TeacherBaseInfoRequest requestForUserFromUserService(@PathVariable("id") Long userId);
}
