package com.example.backend.dto;

/** カートの行単位DTO（商品・単価・数量・小計） */
public record CartItemDto(
        Long cartItemId,
        Long productId,
        String name,
        int price,
        int quantity,
        int subTotal
) {}
