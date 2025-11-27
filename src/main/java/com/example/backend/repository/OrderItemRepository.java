package com.example.backend.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.backend.dto.HistorySummaryItemDto;
import com.example.backend.dto.OrderItemView;
import com.example.backend.entity.OrderItem;

/** 注文明細の永続化＋管理画面向け投影取得 */
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

  /** 管理用：ユーザ×期間で明細を投影取得（OrderItemView） */
  @Query("""
      select new com.example.backend.dto.OrderItemView(
        o.id,
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
        and o.createdAt >= :from
        and o.createdAt <  :to
      order by o.createdAt desc
      """)
  List<OrderItemView> findAdminViewByUserAndPeriod(
      @Param("userId") Long userId,
      @Param("from") LocalDateTime from,
      @Param("to")   LocalDateTime to);

  /** 月次の明細を取得（期間インデックス前提／投影 = OrderItemView） */
  @Query("""
      select new com.example.backend.dto.OrderItemView(
        o.id,
        p.name,
        oi.unitPrice,
        oi.quantity,
        oi.subTotal,
        o.createdAt
      )
      from OrderItem oi
        join oi.order o
        join oi.product p
      where o.createdAt >= :from
        and o.createdAt <  :to
      order by o.createdAt desc
      """)
  List<OrderItemView> findItemsByMonth(
      @Param("from") LocalDateTime from,
      @Param("to")   LocalDateTime to);

  /** ユーザ別・月次の明細（投影 = OrderItemView） */
  @Query("""
      select new com.example.backend.dto.OrderItemView(
        o.id,
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
        and o.createdAt >= :from
        and o.createdAt <  :to
      order by o.createdAt desc
      """)
  List<OrderItemView> findItemsByUserAndMonth(
      @Param("userId") Long userId,
      @Param("from")   LocalDateTime from,
      @Param("to")     LocalDateTime to);

  // 製品がどこかの注文明細に存在するか
  boolean existsByProduct_Id(Long productId);

  // ★追加: 月次の集計（商品×グループ）
  @Query("""
      select new com.example.backend.dto.HistorySummaryItemDto(
        p.name,
        coalesce(g.name, '-'),
        sum(oi.quantity),
        sum(oi.subTotal)
      )
      from OrderItem oi
        join oi.order o
        join oi.product p
        left join p.productGroup g
      where o.createdAt >= :from
        and o.createdAt <  :to
      group by p.name, g.name
      order by sum(oi.quantity) desc
      """)
  List<HistorySummaryItemDto> summarizeByMonth(
      @Param("from") LocalDateTime from,
      @Param("to")   LocalDateTime to);
}
