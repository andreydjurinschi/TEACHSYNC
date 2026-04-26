package com.teachsync.auth.controller;

import com.teachsync.auth.model.LoginRequest;
import com.teachsync.auth.service.AuthService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/teachsync/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginRequest request){
        String token = authService.login(request.getEmail(), request.getPassword());
        return Map.of("token", token);
    }

}
