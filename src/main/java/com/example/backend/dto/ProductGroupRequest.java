package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/** 商品グループ作成/更新リクエスト */
@Getter
@Setter
public class ProductGroupRequest {
    @NotBlank private String name;
    private String managerName;
}
