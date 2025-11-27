/**package com.example.backend.service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.example.backend.dto.ReceiptDto;
import com.example.backend.dto.ReceiptItemDto;
import com.example.backend.entity.Order;
import com.example.backend.entity.OrderItem;
import com.example.backend.repository.OrderRepository;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import lombok.RequiredArgsConstructor;

/**
 * 領収書PDF生成サービス

@Service
@RequiredArgsConstructor
public class ReceiptService {

  private final OrderRepository orderRepository;
  private final TemplateEngine templateEngine; // Thymeleaf

  private static final DateTimeFormatter ORDER_DATETIME_FMT =
      DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
  private static final DateTimeFormatter YEAR_MONTH_FMT =
      DateTimeFormatter.ofPattern("yyyy/MM");
  private static final DateTimeFormatter RECEIPT_NO_DATE_FMT =
      DateTimeFormatter.ofPattern("yyyyMMdd");

  /**

  @Transactional(readOnly = true)
  public byte[] generateReceiptPdf(Long userId, Long orderId) {

    // userId で絞りたい場合は findByIdAndUserId(...) に変更
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new IllegalArgumentException("対象の注文が見つかりません"));


    ReceiptDto dto = toDto(order);

    // --- HTMLレンダリング ---
    org.thymeleaf.context.Context ctx = new Context();
    ctx.setVariable("r", dto); // receipt.html で ${r.xxx} で参照
    String html = templateEngine.process("receipt", ctx);

    // --- HTML → PDF 変換 ---
    try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      PdfRendererBuilder builder = new PdfRendererBuilder();
      builder.useFastMode();
      builder.withHtmlContent(html, null);

      // 日本語フォント（resources/fonts/NotoSansCJKjp-Regular.otf を想定）
      builder.useFont(
          () -> getClass().getResourceAsStream("/fonts/NotoSansCJKjp-Regular.otf"),
          "NotoSansJP"
      );

      builder.toStream(out);
      builder.run();
      return out.toByteArray();
    } catch (Exception e) {
      throw new RuntimeException("領収書PDF生成に失敗しました", e);
    }
  }

  // --- 内部ヘルパ ---


  private ReceiptDto toDto(Order order) {
    String orderDate = order.getCreatedAt().format(ORDER_DATETIME_FMT);
    String yearMonth = order.getCreatedAt().format(YEAR_MONTH_FMT);
    String receiptNumber = buildReceiptNumber(order);

    // 注文明細を DTO へ変換
    List<ReceiptItemDto> items = order.getItems().stream()
        .map(this::toItemDto)
        .toList();

    // 小計 / 送料 / 合計
    int subTotal = order.getItems().stream()
        .mapToInt(OrderItem::getSubTotal)
        .sum();
    int shipping = order.getShipping();   // Order.shipping フィールド
    int total = order.getTotal();         // Order.total フィールド（recalcTotals 済み想定）

    return new ReceiptDto(
        order.getId(),
        orderDate,
        yearMonth,
        receiptNumber,
        "123-3456",                  // 会社郵便番号（仮）
        "東京都XX区XXビル1-2-3",     // 会社住所（仮）
        "株式会社サンプルEC",        // 会社名（仮）
        subTotal,
        shipping,
        total,
        items
    );
  }


  private ReceiptItemDto toItemDto(OrderItem item) {
    String name = item.getProduct().getName(); // Product 名
    int qty = item.getQuantity();
    int unit = item.getUnitPrice();
    int amount = item.getSubTotal();           // 単価×数量（DB保持値）

    return new ReceiptItemDto(name, qty, unit, amount);
  }

  private String buildReceiptNumber(Order order) {
    String date = order.getCreatedAt().format(RECEIPT_NO_DATE_FMT);
    return "R-" + date + "-" + order.getId();
  }
}

*/