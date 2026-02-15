package com.teachsync.AUTH;

import com.teachsync.domain.User;
import com.teachsync.dto.auth.UserRegisterDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/teachsync/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    private final UserAuthService userAuthService;
    private final JwtUtil jwtUtil;
    public AuthController(UserAuthService userAuthService, JwtUtil jwtUtil) {
        this.userAuthService = userAuthService;
        this.jwtUtil = jwtUtil;
    }

    // todo exception
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid UserRegisterDto userRegisterDto){
        userAuthService.register(userRegisterDto);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(null);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String ,String> body){
        String email = body.get("email");
        String password = body.get("password");

        User user = userAuthService.authenticate(email, password);
        String token = jwtUtil.generateToken(user);

        return ResponseEntity.ok(Map.of("token", token));
    }


}
