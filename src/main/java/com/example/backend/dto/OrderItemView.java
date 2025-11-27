// src/main/java/com/example/backend/dto/OrderItemView.java
package com.example.backend.dto;

import java.time.LocalDateTime;

/** 注文明細ビュー（集計/一覧用の投影） */
public record OrderItemView(
    Long orderId,
    String productName,
    int unitPrice,
    int quantity,
    int subTotal,
    LocalDateTime purchasedAt
) {}
