// com.example.backend.repository.OrderHistoryRepository
/* 
package com.example.backend.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.backend.dto.OrderItemView;
import com.example.backend.entity.OrderItem;

注文明細の履歴ビュー投影（集計向け・期間/ユーザ絞り込み） 
public interface OrderHistoryRepository extends JpaRepository<OrderItem, Long> {

  月次の明細を取得（期間インデックス前提／投影 = OrderItemView）
  @Query("""
    select new com.example.backend.dto.OrderItemView(
      p.name,
      oi.unitPrice,
      oi.quantity,
      oi.subTotal,
      o.createdAt
    )
    from OrderItem oi
      join oi.order o
      join oi.product p
    where o.createdAt >= :from and o.createdAt < :to
    order by o.createdAt desc
  """)
  List<OrderItemView> findItemsByMonth(
      @Param("from") LocalDateTime from,
      @Param("to")   LocalDateTime to);

  /** ユーザ別・月次の明細（投影 = OrderItemView）
  @Query("""
    select new com.example.backend.dto.OrderItemView(
      p.name,
      oi.unitPrice,
      oi.quantity,
      oi.subTotal,
      o.createdAt
    )
    from OrderItem oi
      join oi.order o
      join oi.product p
    where o.user.id = :userId
      and o.createdAt >= :from and o.createdAt < :to
    order by o.createdAt desc
  """)
  List<OrderItemView> findItemsByUserAndMonth(
      @Param("userId") Long userId,
      @Param("from")   LocalDateTime from,
      @Param("to")     LocalDateTime to);
}
*/