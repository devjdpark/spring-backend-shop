package com.example.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** 詳細画面に必要な情報を返すDTO */
@Getter
@AllArgsConstructor
public class MyOrderDetailDto {

    private Long orderId;
    private LocalDateTime createdAt;
    private int shipping;
    private int total;
    private String status;
    private List<OrderItemDto> items;

}
