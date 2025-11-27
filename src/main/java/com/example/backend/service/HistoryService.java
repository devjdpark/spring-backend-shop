package com.example.backend.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.dto.HistorySummaryItemDto;
import com.example.backend.dto.HistorySummaryResponseDto;
import com.example.backend.dto.OrderItemView;
import com.example.backend.repository.OrderItemRepository;

import lombok.RequiredArgsConstructor;

/** 履歴情報（一覧＋グラフ用サマリー） */
@Service
@RequiredArgsConstructor
public class HistoryService {

  private final OrderItemRepository orderItemRepository;

  /** 月次の明細一覧（ユーザ指定なし版） */
  @Transactional(readOnly = true)
  public List<OrderItemView> listByYearMonth(String yearMonth) {
    var range = rangeOf(yearMonth);
    return orderItemRepository.findItemsByMonth(range.from, range.to);
  }

  /** 月次サマリー（グラフ用） */
  @Transactional(readOnly = true)
  public HistorySummaryResponseDto summaryByYearMonth(String yearMonth) {
    var range = rangeOf(yearMonth);
    List<HistorySummaryItemDto> items =
        orderItemRepository.summarizeByMonth(range.from, range.to);
    long total = items.stream().mapToLong(HistorySummaryItemDto::getAmount).sum();
    return new HistorySummaryResponseDto(yearMonth, total, items);
  }

  // --- 内部: "YYYY-MM" の境界を返すユーティリティ ---
  private static Range rangeOf(String ymStr) {
    YearMonth ym = YearMonth.parse(ymStr);
    LocalDate d = ym.atDay(1);
    return new Range(d.atStartOfDay(), d.plusMonths(1).atStartOfDay());
  }
  private record Range(LocalDateTime from, LocalDateTime to) {}
}
