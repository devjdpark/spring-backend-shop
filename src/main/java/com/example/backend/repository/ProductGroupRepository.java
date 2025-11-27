package com.example.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.entity.ProductGroup;

/** 商品グループの基本CRUDリポジトリ */
@Repository
public interface ProductGroupRepository extends JpaRepository<ProductGroup, Long> {
  // ★追加: 並び順で一覧取得
  List<ProductGroup> findAllByOrderByOrderIndexAsc();

  // ★追加: 一括で orderIndex を更新（atomic に処理）
  @Modifying
  @Transactional
  @Query("UPDATE ProductGroup g SET g.orderIndex = :orderIndex WHERE g.id = :id")
  int updateOrderIndexById(Long id, int orderIndex);
}
