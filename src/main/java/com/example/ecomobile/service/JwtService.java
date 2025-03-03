package com.example.ecomobile.service;


import com.example.ecomobile.entity.Role;
import com.example.ecomobile.entity.User;
import com.example.ecomobile.enums.RoleName;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class JwtService {

    @Value("${custom.key}")
    private String secretKeyForToken;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(secretKeyForToken.getBytes());
    }


    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("username", user.getUsername());

        StringJoiner sj = new StringJoiner(", ");
        for (Role role : user.getRoles()) {
            sj.add(role.getRoleName().toString());
        }
        claims.put("roles", sj.toString());

        return Jwts.builder()
                .claims(claims)
                .subject(user.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 10*10*10))
                .signWith(secretKey)
                .compact();
    }

    public boolean validate(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public String getUserName(String token) {
        try {
            Claims claims = getClaims(token);

            System.out.println("Claims: " + claims);
            System.out.println("Subject: " + claims.getSubject());

            return claims.getSubject();
        } catch (JwtException e) {
            System.out.println("Xatolik: " + e.getMessage());
            return null;
        }
    }

    private Claims getClaims(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims;
    }

    public List<Role> getRoles(String token) {
        Claims claims = getClaims(token);
        String roles = (String) claims.get("roles");

        return Arrays.stream(roles.split(","))
                .map(item -> {
                    if (Arrays.stream(RoleName.values()).anyMatch(role -> role.name().equals(item.trim()))) {
                        return new Role(null, RoleName.valueOf(item.trim()));
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
