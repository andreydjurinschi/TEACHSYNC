package com.teachsync.interaction.feign.client;

import com.teachsync.interaction.feign.requests.UserRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "users-service", url = "http://localhost:8080/internal/users")
public interface UserAuthClient {
    @GetMapping("/by-email/{email}")
    UserRequest findByEmail(@PathVariable String email);
}
