package com.teachsync.interaction.feign.client;

import com.teachsync.auth.model.AccountInfoResponse;
import com.teachsync.interaction.feign.requests.UserRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "users-service", url = "http://localhost:8080/internal/users")
public interface UserAuthClient {
    @GetMapping("/by-email/{email}")
    UserRequest findByEmail(@PathVariable String email);

    @GetMapping("/account/info")
    AccountInfoResponse getUserInfo(@RequestParam String email);
}
