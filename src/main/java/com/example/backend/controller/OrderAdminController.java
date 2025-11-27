// com.example.backend.controller.OrderAdminController
package com.example.backend.controller;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.OrderItemView;
import com.example.backend.repository.OrderItemRepository;

import lombok.RequiredArgsConstructor;

/**
 * 注文（管理者）に関するHTTPエンドポイントを提供するコントローラ。
 * 役割：管理者の監視・集計用の注文参照を提供。計算はサービスで実施。
 */

@RestController
@RequiredArgsConstructor
public class OrderAdminController {

  private final OrderItemRepository historyRepo;

      /**
     * GET /api/admin/orders を処理する。
     * 目的：指定ユーザ・月の購入履歴（明細）を取得する。
     * 例外：権限不足・入力不正は適切なHTTPステータスへ変換。
     */

  // 月次購入履歴一覧（ユーザ指定可／未指定は全体）
  @GetMapping("/api/admin/orders")
  public ResponseEntity<List<OrderItemView>> list(
      @RequestParam(required = false) Long userId,
      @RequestParam String ym) {

    YearMonth y  = YearMonth.parse(ym);
    LocalDateTime from = y.atDay(1).atStartOfDay();
    LocalDateTime to   = y.plusMonths(1).atDay(1).atStartOfDay();

    List<OrderItemView> result = (userId == null)
        ? historyRepo.findItemsByMonth(from, to)
        : historyRepo.findItemsByUserAndMonth(userId, from, to);

    return ResponseEntity.ok(result);
  }
}
