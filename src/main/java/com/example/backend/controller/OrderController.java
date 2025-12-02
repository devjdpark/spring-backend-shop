package com.example.backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.MyOrderDetailDto;
import com.example.backend.dto.MyOrderListDto;
import com.example.backend.dto.OrderDto;
import com.example.backend.entity.User;
import com.example.backend.service.OrderService;
import com.example.backend.service.ReceiptService;

import lombok.RequiredArgsConstructor;

/**
 * 注文（一般ユーザ）の参照エンドポイント
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService service;
    private final ReceiptService receiptService;

    /**
     * チェックアウト（カートから注文生成）
     */
    @PostMapping("/checkout")
    public ResponseEntity<Map<String, Object>> checkout(
            @AuthenticationPrincipal User principal) {

        if (principal == null) {
            throw new IllegalStateException("未ログインユーザーです。");
        }
        Long orderId = service.checkout(principal.getId());
        return ResponseEntity.ok(Map.of("orderId", orderId));
    }

    /**
     * 注文一覧取得（ログインユーザ）
     */
    @GetMapping
    public ResponseEntity<List<OrderDto>> list(
            @AuthenticationPrincipal User principal) {

        if (principal == null) {
            throw new IllegalStateException("未ログインユーザーです。");
        }
        Long userId = principal.getId();
        List<OrderDto> orders = service.listOrders(userId);
        return ResponseEntity.ok(orders);
    }

    /**
     * 注文詳細取得
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> detail(
            @AuthenticationPrincipal User principal,
            @PathVariable Long id) {

        if (principal == null) {
            throw new IllegalStateException("未ログインユーザーです。");
        }
        Long userId = principal.getId();
        OrderDto dto = service.getOrder(userId, id);
        return ResponseEntity.ok(dto);
    }

    /**
     * 領収書PDFダウンロード
     * GET /api/orders/{id}/receipt
     */
    @GetMapping("/{id}/receipt")
    public ResponseEntity<byte[]> downloadReceipt(
            @AuthenticationPrincipal User principal,
            @PathVariable Long id) {

        if (principal == null) {
            throw new IllegalStateException("未ログインユーザーです。");
        }
        Long userId = principal.getId();

        byte[] pdfBytes = receiptService.generateReceiptPdf(userId, id);
        String filename = "receipt_" + id + ".pdf";

        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + filename + "\""
                )
                .body(pdfBytes);
    }

    /**
     * 領収書PDFをメールで送信
     * POST /api/orders/{id}/send-receipt
     */
    @PostMapping("/{id}/send-receipt")
    public ResponseEntity<Map<String, String>> sendReceiptMail(
            @AuthenticationPrincipal User principal,
            @PathVariable Long id) {

        if (principal == null) {
            throw new IllegalStateException("未ログインユーザーです。");
        }
        Long userId = principal.getId();

        // ユーザ本人チェックはReceiptService内ではなく、
        // コントローラ側で認証済みユーザIDを渡す方針
        receiptService.sendReceiptMail(userId, id);

        return ResponseEntity.ok(Map.of("message", "領収書メールを送信しました。"));
    }


    /**
     * デバッグ用チェックアウト（指定ユーザ）
     */
    @PostMapping("/_debug_checkout/{userPk}")
    public Map<String, Object> debug(@PathVariable Long userPk) {
        Long orderId = service.checkout(userPk);
        return Map.of("orderId", orderId);
    }


    /**
     * Myページ：注文一覧（軽量DTO版）
     * GET /api/orders/my
     */
    @GetMapping("/my")
    public ResponseEntity<List<MyOrderListDto>> myOrders(
            @AuthenticationPrincipal User principal) {

        if (principal == null) {
            throw new IllegalStateException("未ログインユーザーです。");
        }
        Long userId = principal.getId();
        return ResponseEntity.ok(service.listMyOrders(userId));
    }

    /**
     * Myページ：注文詳細
     * GET /api/orders/my/{id}
     */
    @GetMapping("/my/{id}")
    public ResponseEntity<MyOrderDetailDto> myOrderDetail(
            @AuthenticationPrincipal User principal,
            @PathVariable Long id) {

        if (principal == null) {
            throw new IllegalStateException("未ログインユーザーです。");
        }
        Long userId = principal.getId();
        return ResponseEntity.ok(service.getMyOrderDetail(userId, id));
    }


}
