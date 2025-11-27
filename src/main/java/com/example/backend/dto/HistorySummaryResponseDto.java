package com.example.backend.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

/** グラフ表示用の月次サマリー応答 */
@Data
@AllArgsConstructor
public class HistorySummaryResponseDto {
  private String yearMonth;                // "2025-11"
  private long totalAmount;                // 金額合計
  private List<HistorySummaryItemDto> items;
}
