package com.example.backend.controller;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.ProductRequest;
import com.example.backend.entity.Product;
import com.example.backend.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * 商品に関するHTTPエンドポイントを提供するコントローラ。
 * 役割：ページング一覧・詳細・登録/更新/削除のI/O整形。ドメイン処理はサービスへ委譲。
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    // 商品一覧取得
    @GetMapping
    public Page<Product> list(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(name = "page_size", defaultValue = "10") int size,
        @RequestParam(name = "group", required = false) Long groupId
    ) {
        return productService.listProducts(page, size, groupId);
    }
        
    // 商品詳細取得
    @GetMapping("/{id}")
    public Optional<Product> getById(@PathVariable Long id) {
        return productService.findById(id);
    }

    // 商品登録
    @PostMapping
    public ResponseEntity<Product> create(@Valid @RequestBody ProductRequest request) {
        Product saved = productService.createProduct(request);
        return ResponseEntity.ok(saved);
    }

    // 商品更新
    @PutMapping("/{id}")
    public ResponseEntity<Product> update(
            @PathVariable Long id,
            @Validated(ProductRequest.Update.class) @RequestBody ProductRequest req) {
        Product saved = productService.updateProduct(id, req);
        return ResponseEntity.ok(saved);
    }

    // 商品削除
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        productService.deleteProduct(id);
    }
    
}
