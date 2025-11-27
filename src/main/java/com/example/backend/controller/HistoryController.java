package com.example.backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.HistorySummaryResponseDto;
import com.example.backend.dto.OrderItemView;
import com.example.backend.service.HistoryService;

import lombok.RequiredArgsConstructor;

/**
 * 履歴API（V3仕様）
 *  - GET /api/history?yearMonth=YYYY-MM      … 明細一覧（必要なら user クエリも将来対応）
 *  - GET /api/history/summary?yearMonth=…    … グラフ用サマリー
 */
@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class HistoryController {

  private final HistoryService historyService;

  /** 履歴一覧（年-月で絞り込み。未指定なら当月にする等は必要なら拡張） */
  @GetMapping
  public List<OrderItemView> list(
      @RequestParam String yearMonth) {
    return historyService.listByYearMonth(yearMonth);
  }

  /** グラフ用サマリー */
  @GetMapping("/summary")
  public HistorySummaryResponseDto summary(
      @RequestParam String yearMonth) {
    return historyService.summaryByYearMonth(yearMonth);
  }
}
