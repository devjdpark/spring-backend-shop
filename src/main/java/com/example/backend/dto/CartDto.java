package com.example.backend.dto;

import java.util.List;

/** カート全体のレスポンス（明細・送料・合計） */
public record CartDto(
        List<CartItemDto> items,
        long shipping,
        long total
) {}
