package com.example.backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.backend.dto.UserUpdateRequest;
import com.example.backend.entity.User;
import com.example.backend.repository.CartRepository;
import com.example.backend.repository.OrderRepository;
import com.example.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final OrderRepository orderRepository;
  private final CartRepository  cartRepository; 

  // 全ユーザ取得
  public List<User> findAllUsers() {
    return userRepository.findAll();
  }

  // userId 検索（存在しない場合は例外）
  public User findByUserId(String userId) {
    return userRepository.findByUserId(userId)
        .orElseThrow(() -> new RuntimeException("ユーザーが見つかりません: " + userId));
  }

  // ID 検索
  public Optional<User> findById(Long id) {
    return userRepository.findById(id);
  }

  // ユーザ作成（パスワードはBCryptハッシュへ）
  @Transactional
  public User createUser(User user) {
    try {
      if (user.getPassword() == null || user.getPassword().isBlank()) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "パスワードは必須です。");
      }

      String raw = user.getPassword();

      // すでにBCrypt形式かどうかを判定し、平文の場合のみハッシュ化する
      if (!isBcryptEncoded(raw)) {
        user.setPassword(passwordEncoder.encode(raw));
      }

      user.setCreateTime(LocalDateTime.now());
      return userRepository.save(user);

    } catch (DataIntegrityViolationException e) {
      // 例：userId の UNIQUE 制約違反 → 409
      throw new ResponseStatusException(HttpStatus.CONFLICT, "ユーザーIDが既に存在します。", e);
    } catch (DataAccessException e) {
      // DB 例外 → 500
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "ユーザー作成に失敗しました。", e);
    } catch (ResponseStatusException e) {
      throw e;
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "予期しないエラーが発生しました。", e);
    }
  }

  // ユーザ更新（null/空は維持、パスワードは必要時にハッシュ）
  @Transactional
  public User updateUser(Long id, User req) {
    return userRepository.findById(id)
      .map(user -> {
        if (req.getName() != null && !req.getName().isBlank()) {
          user.setName(req.getName());
        }
        if (req.getUserId() != null && !req.getUserId().isBlank()) {
          user.setUserId(req.getUserId());
        }

        // パスワード更新：平文ならハッシュ、既にBCryptならそのまま
        if (req.getPassword() != null && !req.getPassword().isBlank()) {
          String raw = req.getPassword();
          if (!isBcryptEncoded(raw)) {
            user.setPassword(passwordEncoder.encode(raw));
          } else {
            user.setPassword(raw);
          }
        }

        if (req.getAddress() != null) user.setAddress(req.getAddress());
        if (req.getTel() != null)     user.setTel(req.getTel());

        user.setStaff(req.isStaff());

        return userRepository.save(user);
      })
      .orElseThrow(() -> new RuntimeException("ユーザーが見つかりません: " + id));
  }

  // ユーザ削除（参照がある場合は409、最上位管理者は削除不可）
  @Transactional
  public void deleteUser(Long id) {
    // 存在確認
    var user = userRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "ユーザーが見つかりません: " + id));

    // 保護ルール（最上位管理者は削除禁止）
    if (user.isSuperUser()) {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, "最上位管理者は削除できません。");
    }

    // 参照整合性の事前チェック（注文／カート）
    if (orderRepository.existsByUser_Id(id)) {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, "注文データがあるため削除できません。");
    }
    if (cartRepository.existsByUser_Id(id)) {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, "カートが残っているため削除できません。");
    }

    // 実削除（制約違反は409へ）
    try {
      userRepository.delete(user);
    } catch (DataIntegrityViolationException e) {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, "関連データにより削除できません。", e);
    }
  }

  // 自分のプロフィール更新（必要項目のみ更新、パスワードはBCrypt）
  @Transactional
  public void updateMe(User user, UserUpdateRequest req) {
    if (req.getName() != null)    user.setName(req.getName());
    if (req.getAddress() != null) user.setAddress(req.getAddress());
    if (req.getPhone() != null)   user.setTel(req.getPhone());

    // パスワードは入力時のみ更新（空文字は無視）
    if (req.getPassword() != null && !req.getPassword().isBlank()) {
      String raw = req.getPassword();

      // 既にBCrypt形式でハッシュ済みならそのまま、平文ならハッシュ化する
      if (!isBcryptEncoded(raw)) {
        raw = passwordEncoder.encode(raw);
      }
      user.setPassword(raw);
    }

    userRepository.save(user);
  }

  // 既にBCryptでハッシュ化済みかどうかを判定するヘルパー
  private boolean isBcryptEncoded(String rawPassword) {
    if (rawPassword == null) {
      return false;
    }
    // BCryptのハッシュは「$2a$」「$2b$」「$2y$」などのプレフィックスから始まる
    return rawPassword.startsWith("$2a$")
        || rawPassword.startsWith("$2b$")
        || rawPassword.startsWith("$2y$");
  }

}
