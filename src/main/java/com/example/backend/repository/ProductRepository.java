package com.example.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.entity.Product;

/** 商品の永続化（グループ絞り込み・存在/件数・グループ紐づけ削除） */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // グループIDでページング取得
    Page<Product> findByProductGroup_Id(Long groupId, Pageable pageable);

    // グループに属する商品が存在するか
    boolean existsByProductGroup_Id(Long groupId);

    // グループに属する商品の件数
    long countByProductGroup_Id(Long groupId);
    
    // グループ配下の商品を一括削除（外部キー制約に注意）
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    void deleteByProductGroup_Id(Long groupId);
}
