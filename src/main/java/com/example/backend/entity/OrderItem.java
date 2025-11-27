// src/main/java/com/example/backend/entity/OrderItem.java
package com.example.backend.entity;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** 注文明細（購入時の単価・数量・小計を保持） */
@Entity
@Table(name = "order_item")
@Access(AccessType.FIELD)
@Getter @Setter
@NoArgsConstructor
public class OrderItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "order_id")
  private Order order;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "product_id")
  private Product product;

  // 購入時の単価（整数円）
  @Column(name = "unit_price", nullable = false)
  private int unitPrice;

  // 数量
  @Column(nullable = false)
  private int quantity;

  // 小計（固定値としてDB保持）
  @Column(name = "sub_total", nullable = false)
  private int subTotal;

  /** 工場メソッド：作成時に小計を確定 */
  public static OrderItem of(Order order, Product product, int unitPrice, int qty) {
    OrderItem oi = new OrderItem();
    oi.order = order;
    oi.product = product;
    oi.unitPrice = unitPrice;
    oi.quantity = qty;
    oi.subTotal = unitPrice * qty;
    return oi;
  }

  /** 永続前/更新前に小計を同期 */
  @PrePersist
  @PreUpdate
  private void calcSubTotal() {
    this.subTotal = this.unitPrice * this.quantity;
  }

  public int getSubTotal() {
    return this.subTotal;
  }
}
