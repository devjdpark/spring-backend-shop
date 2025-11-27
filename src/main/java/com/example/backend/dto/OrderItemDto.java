// src/main/java/com/example/backend/dto/OrderItemDto.java
package com.example.backend.dto;

/** 注文明細DTO（商品ID・名称・単価・数量・小計） */
public record OrderItemDto(
    Long id,
    Long productId,
    String productName,
    long unitPrice,
    int quantity,
    long subTotal
) {}
