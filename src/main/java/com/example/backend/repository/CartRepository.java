// CartRepository.java
package com.example.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.entity.Cart;

/** カートの永続化リポジトリ（ユーザ単位の取得・存在確認） */
public interface CartRepository extends JpaRepository<Cart, Long> {

  // ユーザごとのカート取得
  Optional<Cart> findByUser_Id(Long userId);

  // 明細IDから親カート取得（サービスの数量変更等で使用）
  Optional<Cart> findByItems_Id(Long itemId);

  // ユーザのカート存在確認
  boolean existsByUser_Id(Long userId);
}
