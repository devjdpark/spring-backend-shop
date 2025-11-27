package com.example.backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.backend.entity.ProductGroup;
import com.example.backend.repository.ProductGroupRepository;
import com.example.backend.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductGroupService {
  
  private final ProductGroupRepository productGroupRepository;
  private final ProductRepository productRepository;

  // グループ一覧
  public List<ProductGroup> findAllGroups() {
    return productGroupRepository.findAll();
  }

  // グループ詳細
  @Transactional(readOnly = true)
  public Optional<ProductGroup> findGroupById(Long id) {
    return productGroupRepository.findById(id);
  }

  // グループ作成
  public ProductGroup createGroup(ProductGroup productGroup) {
    return productGroupRepository.save(productGroup);
  }

  // グループ更新（名前/管理者名のみ反映）
  public ProductGroup updateGroup(Long id, ProductGroup newData) {
    return productGroupRepository.findById(id)
            .map(existing -> {
                existing.setName(newData.getName());
                existing.setManagerName(newData.getManagerName());
                return productGroupRepository.save(existing);
            })
            .orElseThrow(() -> new RuntimeException("productgroupが見つかりません"));
  }

  // グループ削除（子商品の事前削除→本体削除／冪等）
  @Transactional
  public void deleteGroup(Long id) {
    // 存在確認（同時削除を考慮し、なければ冪等に終了）
    var opt = productGroupRepository.findById(id);
    if (opt.isEmpty()) {
      return;
    }

    // 子商品を先に一括削除
    productRepository.deleteByProductGroup_Id(id);

    // 本体削除（並行・制約例外に備える）
    try {
      productGroupRepository.deleteById(id);
    } catch (EmptyResultDataAccessException e) {
      // 先に削除された場合は冪等成功扱い
      return;
    } catch (DataIntegrityViolationException e) {
      // 理論上は子削除済みで起きにくいが保険として409へ
      throw new ResponseStatusException(HttpStatus.CONFLICT, "関連データにより削除できません。", e);
    }
  }

  public List<ProductGroup> findAllGroupsOrdered() {
    return productGroupRepository.findAllByOrderByOrderIndexAsc();
  }

  // ★追加: 並び替え確定（リストの先頭=0 で採番）
  @Transactional
  public void reorderGroups(List<Long> orderedIds) {
    // 入力検証（null・重複など）
    if (orderedIds == null || orderedIds.isEmpty()) return;
    int idx = 0;
    for (Long id : orderedIds) {
      productGroupRepository.updateOrderIndexById(id, idx++);
    }
  }

}
