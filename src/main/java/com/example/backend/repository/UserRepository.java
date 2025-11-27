package com.example.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.backend.entity.User;

/** ユーザの永続化（userId検索を提供） */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  // ログインID（userId）で検索
  Optional<User> findByUserId(String userId);
}
