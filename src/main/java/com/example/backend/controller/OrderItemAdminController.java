package com.example.backend.controller;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.OrderItemView;
import com.example.backend.repository.OrderItemRepository;

import lombok.RequiredArgsConstructor;

/**
 * 注文明細（管理者用）の参照専用コントローラ。
 * 役割：月次／ユーザ別などの購入履歴（明細レベル）の集計データを提供する。
 * 注：注文ヘッダ(Order)の管理（一覧・ステータス更新）は OrderAdminController で実施。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/order-items")
public class OrderItemAdminController {

  private final OrderItemRepository historyRepo;

  /**
   * 月次購入明細一覧（ユーザ指定可）
   * 例：
   *   GET /api/admin/order-items?ym=2025-11
   *   GET /api/admin/order-items?ym=2025-11&userId=3
   */
  @GetMapping
  public ResponseEntity<List<OrderItemView>> list(
    @RequestParam(required = false) Long userId,
    @RequestParam String ym) {

      YearMonth y = YearMonth.parse(ym);
      LocalDateTime from = y.atDay(1).atStartOfDay();
      LocalDateTime to = y.plusMonths(1).atDay(1).atStartOfDay();
      
      List<OrderItemView> result =
          (userId == null)
              ? historyRepo.findItemsByMonth(from, to)
              : historyRepo.findAdminViewByUserAndPeriod(userId, from, to);
    
              return ResponseEntity.ok(result);
      }  
}
