package com.example.backend.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
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

@Service
@RequiredArgsConstructor
public class ReceiptService {

    private final OrderRepository orderRepository;
    private final TemplateEngine templateEngine;
    private final MailService mailService;  // ★ 追加：メール送信用サービス

    // ★ 追加: 領収書用CSSファイル（classpath: pdf/receipt.css）を読み込むためのResource
    @Value("classpath:pdf/receipt.css")
    private Resource receiptCssResource;

    private static final DateTimeFormatter ORDER_DATETIME_FMT =
            DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
    private static final DateTimeFormatter YEAR_MONTH_FMT =
            DateTimeFormatter.ofPattern("yyyy/MM");
    private static final DateTimeFormatter RECEIPT_NO_DATE_FMT =
            DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * 領収書PDFを生成してバイト配列で返す。
     */
    @Transactional(readOnly = true)
    public byte[] generateReceiptPdf(Long userId, Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("対象の注文が見つかりません"));

        ReceiptDto dto = toDto(order);

        // ★ CSSファイルを読み込んで文字列にする
        String css;
        try (InputStream is = receiptCssResource.getInputStream()) {
            css = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("領収書CSSの読み込みに失敗しました", e);
        }

        // --- HTMLレンダリング ---
        Context ctx = new Context();
        ctx.setVariable("r", dto);
        ctx.setVariable("receiptCss", css);  // テンプレート側の <style th:utext="${receiptCss}"> 用

        String html = templateEngine.process("receipt", ctx); // resources/templates/receipt.html

        // --- HTML → PDF 変換 ---
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);

            // 日本語フォント登録 (クラスパス: /fonts/ipaexg.ttf)
            if (getClass().getResource("/fonts/ipaexg.ttf") == null) {
                throw new IllegalStateException("日本語フォントが見つかりません: /fonts/ipaexg.ttf");
            }

            builder.useFont(
                    () -> getClass().getResourceAsStream("/fonts/ipaexg.ttf"),
                    "IPAexGothic"   // ← CSSの font-family と一致させる
            );

            builder.toStream(out);
            builder.run();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("領収書PDF生成に失敗しました", e);
        }
    }

    /**
     * 領収書PDFを生成し、メールで送信するユースケース。
     * コントローラ側でユーザ本人チェック済みであることを前提とする。
     */
    @Transactional(readOnly = true)
    public void sendReceiptMail(Long userId, Long orderId) {

        // 対象注文を取得（ユーザチェックはコントローラ側で済ませる前提）
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("対象の注文が見つかりません"));

        // 宛先メールアドレス（Userエンティティのフィールド名に合わせて修正）
        String to = order.getUser().getUserId(); // ★ email フィールド名に合わせて変更すること

        if (to == null || to.isBlank()) {
            throw new IllegalStateException("ユーザーのメールアドレスが設定されていません。");
        }

        // 領収書PDFを生成
        byte[] pdfBytes = generateReceiptPdf(userId, orderId);

        // 件名・本文・添付ファイル名を組み立て
        String subject = "ご注文の領収書のご案内";
        String bodyText = """
                いつもご利用いただきありがとうございます。
                ご注文の領収書をPDFファイルとして添付しておりますので、ご確認ください。
                """;
        String fileName = "receipt_" + orderId + ".pdf";

        // メール送信
        mailService.sendReceiptMail(to, pdfBytes, fileName, subject, bodyText);
    }

    // ---------- 内部変換ヘルパー ----------

    private ReceiptDto toDto(Order order) {
        String orderDate = order.getCreatedAt().format(ORDER_DATETIME_FMT);
        String yearMonth = order.getCreatedAt().format(YEAR_MONTH_FMT);
        String receiptNumber = buildReceiptNumber(order);

        List<ReceiptItemDto> items = order.getItems().stream()
                .map(this::toItemDto)
                .toList();

        int subTotal = order.getItems().stream()
                .mapToInt(OrderItem::getSubTotal)
                .sum();
        int shipping = order.getShipping();
        int total = order.getTotal();

        return new ReceiptDto(
                order.getId(),
                orderDate,
                yearMonth,
                receiptNumber,
                "123-3456",
                "東京都XX区XXビル1-2-3",
                "株式会社サンプルEC",
                subTotal,
                shipping,
                total,
                items
        );
    }

    private ReceiptItemDto toItemDto(OrderItem item) {
        String name = item.getProduct().getName();
        int qty = item.getQuantity();
        int unit = item.getUnitPrice();
        int amount = item.getSubTotal();
        return new ReceiptItemDto(name, qty, unit, amount);
    }

    private String buildReceiptNumber(Order order) {
        String date = order.getCreatedAt().format(RECEIPT_NO_DATE_FMT);
        return "R-" + date + "-" + order.getId();
    }
}
