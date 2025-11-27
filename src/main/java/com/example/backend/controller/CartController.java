package com.example.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.CartDto;
import com.example.backend.entity.User;
import com.example.backend.service.OrderService;

import lombok.RequiredArgsConstructor;

/**
 * カート／チェックアウトに関するHTTPエンドポイントを提供するコントローラ。
 * 役割：リクエストのバリデーションと入出力整形を行い、在庫検証・決済処理はサービスへ委譲。
 */

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final OrderService service;

    // カート取得
    @GetMapping
    public ResponseEntity<CartDto> get(@AuthenticationPrincipal User me) {
        return ResponseEntity.ok(service.getCart(me.getId()));
    }

    // 追加
    @PostMapping("/items")
    public ResponseEntity<Void> add(
            @AuthenticationPrincipal User me,
            @RequestBody AddReq req) {
        service.addToCart(me.getId(), req.productId(), req.qty());
        return ResponseEntity.ok().build();
    }

    // 数量変更
    @PatchMapping("/items/{id}")
    public ResponseEntity<Void> change(@PathVariable Long id, @RequestBody ChangeReq req) {
        service.changeQty(id, req.qty());
        return ResponseEntity.ok().build();
    }

    // 削除
    @DeleteMapping("/items/{id}")
    public ResponseEntity<Void> remove(@PathVariable Long id) {
        service.removeItem(id);
        return ResponseEntity.ok().build();
    }

    // --- Request DTO ---
    public record AddReq(Long productId, int qty) {}
    public record ChangeReq(int qty) {}
}
