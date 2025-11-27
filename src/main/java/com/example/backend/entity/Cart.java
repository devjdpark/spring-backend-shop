// src/main/java/com/example/backend/entity/Cart.java
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

/** ショッピングカート（ユーザ所有・明細を保持） */
@Entity
@Table(name = "cart")
@Getter @Setter
@NoArgsConstructor
public class Cart {

  // 主キー
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // 所有者（ユーザ）
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id")
  private User user;

  // カート内の明細（親子関係）
  @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<CartItem> items = new ArrayList<>();

  // 監査用
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

  // 便利メソッド（双方向関連を整える）
  public void addItem(CartItem item) {
    item.setCart(this);
    this.items.add(item);
  }

  public void removeItem(CartItem item) {
    this.items.remove(item);
    item.setCart(null);
  }

  public boolean isEmpty() {
    return this.items == null || this.items.isEmpty();
  }

  /** カートを空にする（orphanRemoval=true でDBからも削除） */
  public void clear() {
    if (this.items != null) {
      this.items.clear();
    }
  }
}
