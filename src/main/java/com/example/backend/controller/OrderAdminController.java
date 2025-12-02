// com.example.backend.controller.OrderAdminController
package com.example.backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.entity.Order;
import com.example.backend.entity.OrderStatus;
import com.example.backend.service.OrderAdminService;

import lombok.RequiredArgsConstructor;

/**
 * 注文ヘッダ（管理者用）の管理コントローラ。
 * 一覧取得・ステータス更新を提供する。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/orders")
public class OrderAdminController {

  private final OrderAdminService adminService;

    /** 全注文一覧（ID降順） */
    @GetMapping
    public ResponseEntity<List<Order>> listAll() {
      return ResponseEntity.ok(adminService.listAllOrders());
    }

    /** ステータス更新 */
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(
        @PathVariable Long id,
        @RequestBody Map<String, String> body) {

      String statusStr = body.get("status");
      OrderStatus status = OrderStatus.valueOf(statusStr);
      adminService.updateStatus(id, status);

        return ResponseEntity.ok().build();
      }
}
