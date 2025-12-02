package com.example.backend.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** 一覧用の注文ヘッダDTO */
@Getter
@AllArgsConstructor
public class MyOrderListDto {
    private Long orderId;
    private LocalDateTime createdAt;
    private int total;
    private String status;
}
