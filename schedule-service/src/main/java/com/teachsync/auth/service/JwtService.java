package com.teachsync.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.List;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    private Key getKeySign(){
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String extractUsername(String token){
        return extractClaimsFromToken(token).getSubject();
    }

    public List<SimpleGrantedAuthority> extractAuthorities(String token){
        String role = extractClaimsFromToken(token).get("roles").toString();
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    private Claims extractClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKeySign())
                .build()
                .parseClaimsJws(token).getBody();
    }
}
