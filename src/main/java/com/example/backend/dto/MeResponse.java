// src/main/java/com/example/backend/dto/MeResponse.java
package com.example.backend.dto;

import java.util.List;

/** 自分の基本情報レスポンス（ID／ユーザーID／氏名／ロール） */
public record MeResponse(Long id, String userId, String name, List<String> roles) {}
