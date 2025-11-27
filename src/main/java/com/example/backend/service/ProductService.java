package com.example.backend.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.backend.dto.ProductRequest;
import com.example.backend.entity.Product;
import com.example.backend.entity.ProductGroup;
import com.example.backend.repository.CartItemRepository;
import com.example.backend.repository.OrderItemRepository;
import com.example.backend.repository.ProductGroupRepository;
import com.example.backend.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {
  
  private final ProductRepository productRepository;
  private final ProductGroupRepository productGroupRepository;
  private final OrderItemRepository orderItemRepository;
  private final CartItemRepository cartItemRepository;  

  // 旧APIの互換用（ページ番号1始まり）
  public Page<Product> getProducts(Long groupId, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        if (groupId != null) {
            return productRepository.findByProductGroup_Id(groupId, pageable);
        }
        return productRepository.findAll(pageable);
  }

  // 単一取得
  public Optional<Product> findById(Long id) {
    return productRepository.findById(id);
  }

  // 一覧（ページング、降順ID／グループ絞り込み可）
  @Transactional(readOnly = true)
  public Page<Product> listProducts(int page, int size, Long groupId) {
    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("id")));
    if (groupId == null) {
      return productRepository.findAll(pageable);
    }
    return productRepository.findByProductGroup_Id(groupId, pageable);
  }

  // 作成
  @Transactional
  public Product createProduct(ProductRequest request) {
      System.out.println("要求値: " + request);
      System.out.println("groupId: " + request.getGroupId());
      
      ProductGroup group = productGroupRepository.findById(request.getGroupId())
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "グループIDが見つかりません: " + request.getGroupId()
        ));

      Product product = Product.builder()
              .name(request.getName())
              .price(request.getPrice())
              .stock(request.getStock())
              .productGroup(group)
              .createTime(LocalDateTime.now())
              .build();

      return productRepository.save(product);
  }

  // 更新（null/空は維持、数値は正のときのみ反映、groupId指定時のみ所属変更）
  @Transactional
  public Product updateProduct(Long id, ProductRequest req) {
    try {
      // 対象取得（なければ404）
      Product product = productRepository.findById(id)
          .orElseThrow(() -> new ResponseStatusException(
              HttpStatus.NOT_FOUND, "商品が見つかりません: " + id));

      // 名前：null/空は現値維持
      if (req.getName() != null && !req.getName().isBlank()) {
        product.setName(req.getName());
      }

      // 在庫／価格：正のときのみ反映（0以下は維持）
      if (req.getStock() != null && req.getStock() > 0) {
        product.setStock(req.getStock());
      }
      if (req.getPrice() != null && req.getPrice() > 0) {
        product.setPrice(req.getPrice());
      }

      // グループ：groupIdが渡された場合のみ差し替え（nullは維持）
      if (req.getGroupId() != null) {
        ProductGroup g = productGroupRepository.findById(req.getGroupId())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "グループが見つかりません: " + req.getGroupId()));
        product.setProductGroup(g);
      }

      // 保存
      return productRepository.save(product);

    } catch (ResponseStatusException e) {
      // 業務エラーはそのまま再送出
      throw e;
    } catch (DataAccessException e) {
      // DBアクセス系 → 500
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "更新に失敗しました。", e);
    } catch (Exception e) {
      // 想定外 → 500
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "予期しないエラーが発生しました。", e);
    }
  }

  // 削除（参照存在時は409）
  @Transactional
  public void deleteProduct(Long id) {
    // 存在確認
    var product = productRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "商品が見つかりません: " + id));

    // 参照チェック（注文／カートに存在する場合は削除不可）
    if (orderItemRepository.existsByProduct_Id(id)) {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, "注文に使用された商品は削除できません。");
    }
    if (cartItemRepository.existsByProduct_Id(id)) {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, "カートに入っているため削除できません。");
    }

    // 実削除（制約違反は409へ）
    try {
      productRepository.delete(product);
    } catch (DataIntegrityViolationException e) {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, "関連データにより削除できません。", e);
    }
  }
}
