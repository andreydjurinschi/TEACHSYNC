package com.teachsync.jwtService;

import com.teachsync.interaction.feign.requests.UserRequest;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    private long expiration;

    private Key getSignKey(){
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(UserRequest user){
        String subjectUsername = user.getName().concat(".").concat(user.getSurname());
        return Jwts.builder()
                .setSubject(subjectUsername)
                .claim("roles", user.getRole().name())
                .claim("userId", user.getId())
                .claim("email", user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}
