package com.example.backend.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.backend.entity.Order;
import com.example.backend.entity.OrderStatus;
import com.example.backend.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

/** 管理者専用の注文管理ロジック（状態変更など） */
@Service
@RequiredArgsConstructor
public class OrderAdminService {
  private final OrderRepository orderRepo;

  /** 全注文一覧をID降順で返却 */
  @Transactional(readOnly = true)
  public List<Order> listAllOrders(){
    return orderRepo.findAllByOrderByIdDesc();
  }

  /** ステータス更新（管理者） */
  @Transactional
  public void updateStatus(Long orderId, OrderStatus status){
    Order o = orderRepo.findById(orderId)
      .orElseThrow(() -> new ResponseStatusException(
        HttpStatus.NOT_FOUND, "注文が見つかりません"));

        o.setStatus(status);
        orderRepo.save(o);
  }
}
