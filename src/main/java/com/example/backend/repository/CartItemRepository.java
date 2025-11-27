// CartItemRepository.java
package com.example.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.entity.CartItem;

/** カート明細の永続化リポジトリ（基本CRUD＋存在確認） */
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

  // カートIDで明細一覧
  List<CartItem> findByCartId(Long cartId);

  // 製品がどこかのカートに存在するか
  boolean existsByProduct_Id(Long productId);
}
