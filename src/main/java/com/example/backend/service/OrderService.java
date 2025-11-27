package com.example.backend.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.backend.dto.CartDto;
import com.example.backend.dto.CartItemDto;
import com.example.backend.dto.OrderDto;
import com.example.backend.dto.OrderItemDto;
import com.example.backend.entity.Cart;
import com.example.backend.entity.CartItem;
import com.example.backend.entity.Order;
import com.example.backend.entity.Product;
import com.example.backend.repository.CartRepository;
import com.example.backend.repository.OrderRepository;
import com.example.backend.repository.ProductRepository;
import com.example.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepo;
    private final CartRepository cartRepo;
    private final UserRepository userRepo;
    private final ProductRepository productRepo;

    // カートを取得（なければ作成して返す）
    /** カートがなければ新規作成して返すユーティリティ。 */
    @Transactional
    protected Cart getOrCreateCart(Long userId) {
        return cartRepo.findByUser_Id(userId).orElseGet(() -> {
            Cart c = new Cart();
            c.setUser(userRepo.getReferenceById(userId));
            c.setCreatedAt(LocalDateTime.now());   // LocalDateTimeで作成時刻を設定
            c.setUpdatedAt(LocalDateTime.now());   // LocalDateTimeで更新時刻を設定
            return cartRepo.save(c);
        });
    }

    // username から userId を解決
    @Transactional(readOnly = true)
    public Long resolveUserId(String username) {
      return userRepo.findByUserId(username)
          .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
              org.springframework.http.HttpStatus.NOT_FOUND, "ユーザーが見つかりません。"))
          .getId();
    }

    // カート参照（DTOに変換して返却）
    @Transactional
    public CartDto getCart(Long userId) {
        Cart cart = getOrCreateCart(userId);

        List<CartItemDto> items = cart.getItems().stream()
            .map(it -> {
                int unitPrice = it.getProduct().getPrice();     // 単価はProductから取得
                int qty       = it.getQuantity();
                int subTotal  = unitPrice * qty;                // 合計（小計）はintで算出
                return new CartItemDto(
                    it.getId(),
                    it.getProduct().getId(),
                    it.getProduct().getName(),
                    unitPrice,
                    qty,
                    subTotal
                );
            })
            .toList();

        int shipping = calcShipping(items);                     // 送料の算出（要件に応じて調整）
        int total    = items.stream().mapToInt(CartItemDto::subTotal).sum() + shipping;

        return new CartDto(items, shipping, total);
    }

    // カートに追加（同一商品は数量加算）
    @Transactional
    public void addToCart(Long userId, Long productId, int qty) {
        if (qty <= 0) throw new IllegalArgumentException("qty must be positive");

        Cart cart = getOrCreateCart(userId);
        Product p = productRepo.findById(productId).orElseThrow();

        CartItem item = cart.getItems().stream()
            .filter(i -> i.getProduct().getId().equals(productId))
            .findFirst()
            .orElseGet(() -> {
                CartItem ni = new CartItem();
                ni.setCart(cart);
                ni.setProduct(p);
                ni.setQuantity(0);
                cart.addItem(ni); // 双方向の便宜メソッドを使用
                return ni;
            });

        item.setQuantity(item.getQuantity() + qty);
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepo.save(cart);
    }

    // 数量変更
    @Transactional
    public void changeQty(Long cartItemId, int qty) {
        // カート項目IDから親カートを引く（必要ならリポジトリにクエリメソッドを用意）
        Cart cart = cartRepo.findByItems_Id(cartItemId)
            .orElseThrow();
        cart.getItems().stream()
            .filter(i -> i.getId().equals(cartItemId))
            .findFirst()
            .ifPresent(i -> i.setQuantity(qty));
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepo.save(cart);
    }

    // アイテム削除
    @Transactional
    public void removeItem(Long cartItemId) {
        Cart cart = cartRepo.findByItems_Id(cartItemId)
            .orElseThrow();
        cart.getItems().removeIf(i -> i.getId().equals(cartItemId));
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepo.save(cart);
    }

    // チェックアウト（在庫確認→注文作成→在庫差引→カートクリア）
    @Transactional
    public Long checkout(Long userId) {
      try {
        Cart cart = cartRepo.findByUser_Id(userId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "カートが見つかりません。"));

        if (cart.isEmpty()) {
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "カートが空です。");
        }
        if (!cart.getUser().getId().equals(userId)) {
          throw new ResponseStatusException(HttpStatus.FORBIDDEN, "他ユーザーのカートです。");
        }

        // 在庫チェック（全アイテム）
        for (CartItem ci : cart.getItems()) {
          if (ci.getProduct().getStock() < ci.getQuantity()) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT, ci.getProduct().getName() + " の在庫不足");
          }
        }

        // 注文作成＋在庫差し引き
        Order order = new Order();
        order.setUser(cart.getUser());
        for (CartItem ci : cart.getItems()) {
          Product p = ci.getProduct();
          p.decreaseStock(ci.getQuantity());      // 在庫を減算
          order.addItem(p, ci.getQuantity());     // 注文明細に追加
        }
        order.recalcTotals();                      // 合計再計算
        orderRepo.save(order);
        cart.clear();                              // カートを空にする

        return order.getId();

      } catch (ResponseStatusException e) {
        // 意図した業務エラーはそのまま再送出
        throw e;
      } catch (DataAccessException e) {
        // DB系の例外 → 500
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "チェックアウトに失敗しました。", e);
      } catch (Exception e) {
        // 想定外 → 500
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "予期しないエラーが発生しました。", e);
      }
    }
    
    // 送料計算（要件に合わせて実装／未使用なら0固定）
    private int calcShipping(List<CartItemDto> items) {
        return 0;
    }

    // 注文一覧（ユーザID指定）
    @Transactional(readOnly = true)
    public List<OrderDto> listOrders(Long userId) {
      return orderRepo.findByUserIdOrderByIdDesc(userId).stream()
          .map(this::toDto)
          .toList();
    }

    // 注文詳細（ユーザ所有チェック込み）
    @Transactional(readOnly = true)
    public OrderDto getOrder(Long userId, Long orderId) {
      Order o = orderRepo.findByIdAndUserId(orderId, userId).orElseThrow();
      return toDto(o);
    }

    // Entity→DTO 変換（共通化）
    private OrderDto toDto(Order o) {
      var items = o.getItems().stream()
          .map(oi -> new OrderItemDto(
              oi.getId(),
              oi.getProduct().getId(),
              oi.getProduct().getName(),
              oi.getUnitPrice(),
              oi.getQuantity(),
              oi.getSubTotal()
          ))
          .toList();

      return new OrderDto(
          o.getId(),
          o.getCreatedAt(),
          o.getShipping(),
          o.getTotal(),
          items
      );
    }
}
