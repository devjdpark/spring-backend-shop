// src/main/java/com/example/backend/entity/Order.java
package com.example.backend.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** 注文ヘッダ（合計・送料・明細を保持） */
@Entity
@Table(name = "orders")
@Getter @Setter
@NoArgsConstructor
public class Order {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id")
  private User user;

  // 明細（親子関係）
  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<OrderItem> items = new ArrayList<>();

  // 送料（整数円）
  @Column(nullable = false)
  private int shipping = 0;

  // 合計金額（整数円）
  @Column(nullable = false)
  private int total = 0;

  // 監査
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @PrePersist
  void onCreate() {
    this.createdAt = LocalDateTime.now();
    this.updatedAt = this.createdAt;
  }

  @PreUpdate
  void onUpdate() {
    this.updatedAt = LocalDateTime.now();
  }

  // 双方向関連ヘルパ
  public void addItem(OrderItem item) {
    item.setOrder(this);
    this.items.add(item);
  }

  public void removeItem(OrderItem item) {
    this.items.remove(item);
    item.setOrder(null);
  }

  // 商品から明細を追加（単価は現在のProduct.price）
  public void addItem(Product p, int qty) {
    // Product.getPrice()が long の場合に備え、intへ安全に変換
    int unitPrice = Math.toIntExact(p.getPrice());
    OrderItem oi = OrderItem.of(this, p, unitPrice, qty);
    this.addItem(oi);
    recalcTotals();
  }

  // 合計の再計算（送料は別ポリシーで設定）
  public void recalcTotals() {
    int itemsTotal = this.items.stream().mapToInt(OrderItem::getSubTotal).sum();
    int shipping = 0; // ポリシー適用時に調整
    this.shipping = shipping;
    this.total = itemsTotal + shipping;
  }
}
