package com.example.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.ProductGroupReorderRequest;
import com.example.backend.dto.ProductGroupRequest;
import com.example.backend.entity.ProductGroup;
import com.example.backend.service.ProductGroupService;

import lombok.RequiredArgsConstructor;

/**
 * 商品グループに関するHTTPエンドポイントを提供するコントローラ。
 * 役割：分類のCRUDの入口。ビジネスルールはサービス層へ委譲。
 */

@RestController
@RequestMapping("/api/productgroups")
@RequiredArgsConstructor
public class ProductGroupController {

  private final ProductGroupService productGroupService;

  // 商品グループ一覧取得
  @GetMapping
  public List<ProductGroup> getAll() {
    return productGroupService.findAllGroups();
  }

  // 商品グループ詳細取得
  @GetMapping("/{id}")
  public ResponseEntity<ProductGroup> getGroupById(@PathVariable Long id) {
    return productGroupService.findGroupById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  // 商品グループ登録
  @PostMapping
  public ResponseEntity<ProductGroup> createGroup(@RequestBody ProductGroupRequest request) {
      ProductGroup group = new ProductGroup();
      group.setName(request.getName());
      group.setManagerName(request.getManagerName());
      return ResponseEntity.ok(productGroupService.createGroup(group));
  }

  // 商品グループ更新
  @PutMapping("/{id}")
  public ProductGroup updateGroup(@PathVariable Long id, @RequestBody ProductGroupRequest request) {
      ProductGroup newData = new ProductGroup();
      newData.setName(request.getName());
      newData.setManagerName(request.getManagerName());
      return productGroupService.updateGroup(id, newData);
  }

  // ★追加: 並び順で取得
  @GetMapping(params = "sort=order")
  public List<ProductGroup> getAllOrdered() {
    return productGroupService.findAllGroupsOrdered();
  }

  // ★追加: 並び替え確定（管理者のみ想定）
  @PutMapping("/reorder")
  public ResponseEntity<Void> reorder(@RequestBody ProductGroupReorderRequest req) {
    // 入力が空なら何もしない（冪等）
    productGroupService.reorderGroups(req.getOrderedIds());
    return ResponseEntity.noContent().build();
  }

  // 商品グループ削除
  @DeleteMapping("/{id}")
  public void deleteGroup(@PathVariable Long id){
    productGroupService.deleteGroup(id);
  }
}
