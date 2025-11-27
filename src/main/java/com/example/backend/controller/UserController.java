package com.example.backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.UserUpdateRequest;
import com.example.backend.entity.User;
import com.example.backend.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * ユーザー管理に関するHTTPエンドポイントを提供するコントローラ。
 * 役割：管理者のユーザーCRUDと、本人のプロフィール参照/更新を提供。
 */

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  // ユーザー一覧取得
  @GetMapping
  public List<User> getAllUsers() {
    return userService.findAllUsers();
  }

  // ユーザー登録
  @PostMapping
  public User createUser(@Valid @RequestBody User user) {
    return userService.createUser(user);
  }

  // ユーザー更新
  @PutMapping("/{id}")
  public User updatUser(@PathVariable Long id, @Valid @RequestBody User updatedUser) {
      return userService.updateUser(id, updatedUser);
  }

  // ユーザー削除
  @DeleteMapping("/{id}")
  public void deleteUser(@PathVariable Long id) {
    userService.deleteUser(id);
  }

  // 自分のプロフィール取得
  @GetMapping("/me")
  public ResponseEntity<?> getMe(Authentication authentication) {
      if (authentication == null || authentication.getPrincipal() == null) {
          return ResponseEntity.status(401).body("Not authenticated");
      }

      User user = (User) authentication.getPrincipal();

      return ResponseEntity.ok(Map.of(
          "userId", user.getUserId(),
          "name", user.getName(),
          "address", user.getAddress(),
          "phone", user.getTel(),  
          "isStaff", user.isStaff(),
          "superUser", user.isSuperUser()
      ));
  }

  // ユーザー詳細取得
  @GetMapping("/{id}")
  public ResponseEntity<User> getUserById(@PathVariable Long id) {
      return userService.findById(id)
              .map(ResponseEntity::ok)
              .orElse(ResponseEntity.notFound().build());
  }

  // 自分のプロフィール更新
  @PutMapping("/me")
  public ResponseEntity<?> updateMe(Authentication auth, @Valid @RequestBody UserUpdateRequest req) {
    Object principal = auth.getPrincipal();
    if (!(principal instanceof User)) {
      return ResponseEntity.status(401).body("Unauthorized");
    }
    User user = (User) principal;

    userService.updateMe(user, req);

    return ResponseEntity.ok(Map.of("message", "ユーザー情報が更新されました"));
  }

}
