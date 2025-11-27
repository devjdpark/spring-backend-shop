package com.example.backend.service;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    /**
     * PDFファイルを添付した領収書メールを送信する共通メソッド。
     *
     * @param to        宛先メールアドレス
     * @param pdfBytes  添付するPDFのバイト配列
     * @param fileName  添付ファイル名（例: receipt_123.pdf）
     * @param subject   メールの件名
     * @param bodyText  メール本文（プレーンテキスト）
     */
    public void sendReceiptMail(
            String to,
            byte[] pdfBytes,
            String fileName,
            String subject,
            String bodyText
    ) {
        try {
            // マルチパートメール（添付ファイルあり）を作成
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            // 宛先・件名・本文を設定
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(bodyText, false); // false = プレーンテキスト

            // PDF添付（nullや空配列の場合は添付しない）
            if (pdfBytes != null && pdfBytes.length > 0) {
                helper.addAttachment(fileName, new ByteArrayResource(pdfBytes));
            }

            // メール送信実行
            mailSender.send(message);

        } catch (Exception e) {
            // 必要に応じてログ出力や、送信履歴テーブルへの保存などを行う
            throw new IllegalStateException("領収書メールの送信に失敗しました。", e);
        }
    }
}
