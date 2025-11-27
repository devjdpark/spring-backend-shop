package com.example.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

/** 商品作成/更新リクエスト（Create必須・Updateはnull維持） */
@Getter
@Setter
public class ProductRequest {

    @NotBlank(groups = Create.class, message = "商品名は必須です。")
    private String name;

    @NotNull(groups = Create.class, message = "価格は必須です。")
    @Positive(message = "価格は正の整数である必要があります。")
    private Integer price;

    @NotNull(groups = Create.class, message = "在庫数量は必須です。")
    @Positive(message = "在庫数量は正の整数である必要があります。")
    private Integer stock;

    @NotNull(groups = Create.class, message = "グループID（groupId）は必須です。")
    @JsonProperty("groupId")
    private Long groupId;

    public interface Create {}
    public interface Update {}
}
