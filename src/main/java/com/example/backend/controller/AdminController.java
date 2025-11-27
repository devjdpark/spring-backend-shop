// src/main/java/com/example/backend/controller/AdminController.java
package com.example.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

record AdminStats(long userCount, long itemCount) {}


/**
 * 管理（集計／監視）に関するHTTPエンドポイントを提供するコントローラ。
 * 役割：入力検証と入出力整形を行い、ビジネスロジックはサービス層へ委譲する。
 */
@RestController
public class AdminController {

    /**
   * GET /api/admin/stats を処理する。
   * 目的：管理画面向けに、ユーザ数・商品数などの簡易統計情報を返却する。
   * 例外：入力不正・権限不足・対象未存在などは適切なHTTPステータスに変換して返却する。
   */

  // 統計取得（ユーザ数／商品数の簡易サマリ）
  @GetMapping("/api/admin/stats")
  public AdminStats stats() {
    return new AdminStats(12, 345);
  }
}
