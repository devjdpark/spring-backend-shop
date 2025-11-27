// src/main/java/com/example/backend/dto/OrderDto.java
package com.example.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

/** 注文レスポンス（ヘッダ＋明細一覧） */
public record OrderDto(
    Long orderId,
    LocalDateTime orderedAt,
    long shipping,
    long total,
    List<OrderItemDto> items
) {}
