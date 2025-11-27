// src/main/java/com/example/backend/repository/OrderRepository.java
package com.example.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.entity.Order;

/** 注文ヘッダの永続化（ユーザ別取得・所有チェック） */
public interface OrderRepository extends JpaRepository<Order, Long> {

  // ユーザ別にID降順で一覧
  List<Order> findByUserIdOrderByIdDesc(Long userId);

  // ユーザ所有の単一注文
  Optional<Order> findByIdAndUserId(Long id, Long userId);

  // ユーザに紐づく注文の存在チェック
  boolean existsByUser_Id(Long userId);
}
