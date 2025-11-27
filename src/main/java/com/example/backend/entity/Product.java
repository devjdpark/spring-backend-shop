// src/main/java/com/example/backend/entity/Product.java
package com.example.backend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** 商品（価格・在庫・所属グループを管理） */
@Builder
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "item")
public class Product {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // 表示名
  @Column(nullable = false)
  private String name;

  // 所属グループ
  @ManyToOne
  @JoinColumn(name = "group_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_item_group"))
  private ProductGroup productGroup;

  // 在庫数量（0以上）
  private int stock;

  // 価格（整数円）
  private int price;

  // 作成時刻（アプリ側で設定）
  @Builder.Default
  @Column(name = "create_time")
  private LocalDateTime createTime = LocalDateTime.now();

  // 在庫を減算（業務ガード付き）
  public void decreaseStock(int qty) {
    if (qty <= 0) throw new IllegalArgumentException("qty > 0 が必要です。");
    if (this.stock < qty) {
      throw new IllegalStateException(this.name + " の在庫不足");
    }
    this.stock -= qty;
  }
}
