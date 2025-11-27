package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/** グラフの1要素（商品×グループの月次集計） */
@Data
@AllArgsConstructor
public class HistorySummaryItemDto {
  private String label;  // 商品名
  private String group;  // グループ名（nullは"-"に統一）
  private Long qty;      // 数量合計
  private Long amount;   // 金額合計
}
