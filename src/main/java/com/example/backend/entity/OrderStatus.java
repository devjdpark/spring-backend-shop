package com.example.backend.entity;

public enum OrderStatus {
    ORDERED,        // 注文済み（初期状態）
    PAID,           // 決済完了
    SHIPPED,        // 発送済み
    COMPLETED,      // 配送完了/受取完了
    CANCELLED       // キャンセル

}
