package com.teachsync.auth.service;

import com.teachsync.interaction.feign.client.UserAuthClient;
import com.teachsync.interaction.feign.requests.UserRequest;
import com.teachsync.jwtService.JwtService;
import com.teachsync.utils.PasswordUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserAuthClient userAuthClient;
    private final JwtService jwtService;

    public AuthService(UserAuthClient userAuthClient, JwtService jwtService) {
        this.userAuthClient = userAuthClient;
        this.jwtService = jwtService;
    }

    public String login(String email, String password){
        UserRequest userRequest = userAuthClient.findByEmail(email);
        if(userRequest == null || !PasswordUtils.verify(password, userRequest.getPassword())){
            throw new BadCredentialsException("Invalid email or password");
        }
        return jwtService.generateToken(userRequest);
    }
}
