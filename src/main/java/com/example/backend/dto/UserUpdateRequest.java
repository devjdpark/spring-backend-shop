package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/** 自分のプロフィール更新リクエスト（名前必須） */
@Getter
@Setter
public class UserUpdateRequest {
    @NotBlank 
    private String name;

    @Size(max = 100)
    private String address;
    
    private String phone;
    private String password;
}
