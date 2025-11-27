// src/main/java/com/example/backend/entity/CartItem.java
package com.example.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** カートの明細（商品と数量） */
@Entity
@Table(
  name = "cart_item",
  // 同一カート内で同じ商品を重複登録しないための一意制約
  uniqueConstraints = @UniqueConstraint(name = "uk_cart_product", columnNames = { "cart_id", "product_id" })
)
@Getter @Setter
@NoArgsConstructor
public class CartItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // 親（カート）
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "cart_id")
  private Cart cart;

  // 商品
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "product_id")
  private Product product;

  // 数量（1以上を想定）
  @Column(nullable = false)
  private int quantity;
}
