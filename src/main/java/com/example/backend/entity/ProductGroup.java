// src/main/java/com/example/backend/entity/ProductGroup.java
package com.example.backend.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** 商品グループ（分類の親。商品の集合を持つ） */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "itemgroup")
@AllArgsConstructor
@Builder
public class ProductGroup {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // グループ名（一意）
  @Column(nullable = false, unique = true)
  private String name;

  // 作成時刻
  @Builder.Default
  @Column(name = "create_time", nullable =  false)
  private LocalDateTime createTime = LocalDateTime.now();

  // 管理者名（任意）
  @Column(name = "manager_name", nullable = true, length = 100)
  private String managerName;

  // 配下の商品一覧（親子関係）
  @Builder.Default
  @OneToMany(mappedBy = "productGroup", cascade = CascadeType.ALL, orphanRemoval = true)
  @com.fasterxml.jackson.annotation.JsonIgnore
  private List<Product> items = new ArrayList<>();

  // ★変更点: 並び順の保持用。既存機能へ影響しないデフォルト値を設定
  // 表示や登録時のグループ選択順を一元管理するために追加
  @Builder.Default
  @Column(name = "order_index", nullable = false)
  private int orderIndex = 0;
  }
