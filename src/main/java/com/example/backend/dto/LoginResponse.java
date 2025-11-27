package com.example.backend.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** ログイン応答（アクセストークン等） */
@Getter 
@AllArgsConstructor
public class LoginResponse {
  private String accessToken;
  private String tokenType;  // "Bearer"
  private long   expiresIn;  // seconds
  private Long   userId;
  private String name;
  private List<String> roles;
}
