package com.example.backend.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.config.JwtProvider;
import com.example.backend.dto.LoginRequest;
import com.example.backend.dto.LoginResponse;
import com.example.backend.entity.User;
import com.example.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * 認証・ログインに関するHTTPエンドポイントを提供するコントローラ。
 * 役割：資格情報の検証とトークン発行の入口。ドメインロジックはサービス層へ委譲。
 */

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwt;

    // ログイン（JWT発行）
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        User user = userRepository.findByUserId(req.getUserId()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid credentials"));
        }

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid credentials"));
        }

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        if (user.isSuperUser()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_SUPERUSER"));
        }
        if (user.isStaff()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }

        String token = jwt.generateToken(user);

        System.out.println("入力されたパスワード: " + req.getPassword());
        System.out.println("DB非番ハッシュ: " + user.getPassword());
        System.out.println("パスワードマッチング結果: " + passwordEncoder.matches(req.getPassword(), user.getPassword()));

        List<String> roleNames = authorities.stream()
                .map(SimpleGrantedAuthority::getAuthority)
                .toList();

        return ResponseEntity.ok(new LoginResponse(
                token,
                "Bearer",
                jwt.getExpSecs(),
                user.getId(),
                user.getName(),
                roleNames
        ));
    }
}
