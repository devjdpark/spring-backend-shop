package com.example.backend.config;

import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.backend.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

@Component
public class JwtProvider {

    @Value("${app.jwt.secret}")
    private String secretKey;

    @Value("${app.jwt.access-exp-seconds}")
    private long validityInSeconds;

    // SecretKey
    private SecretKey getSigningKey() {
        return new SecretKeySpec(secretKey.getBytes(), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String generateToken(User user) {
        String roles;
        if (user.isSuperUser()) {
            roles = "ROLE_SUPERUSER";
        } else if (user.isStaff()) {
            roles = "ROLE_ADMIN";
        } else {
            roles = "ROLE_USER";
        }

        Date now = new Date();
        Date exp = new Date(now.getTime() + (validityInSeconds * 1000)); 

        return Jwts.builder()
                .subject(user.getUserId())
                .claim("roles", roles)
                .issuedAt(now)
                .expiration(exp)
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }   


    public Jws<Claims> parse(String token) {
        return Jwts.parser()
                .clockSkewSeconds(60)
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token);
    }

    public long getExpSecs() {
        return validityInSeconds;
    }
}
