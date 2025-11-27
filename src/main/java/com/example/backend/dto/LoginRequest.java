// src/main/java/com/example/backend/dto/LoginRequest.java
package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/** ログイン要求（ユーザーID／パスワード） */
@Getter @Setter
public class LoginRequest {
  @NotBlank private String userId;
  @NotBlank private String password;
}
