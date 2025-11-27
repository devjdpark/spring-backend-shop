// src/main/java/com/example/backend/entity/User.java
package com.example.backend.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** ユーザ（認証情報・権限フラグを含む） */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // ログインID（メール等）／DBカラム：user_id
  @Column(name = "user_id", nullable = false, unique = true)
  private String userId;

  private String name;
  private String password;

  // 管理者フラグ
  @Column(name = "is_staff", nullable = false)
  private boolean isStaff = false;

  // 最上位管理者フラグ
  @Column(name = "super_user", nullable = false)
  private boolean superUser = false;

  @JsonProperty("isStaff")
  public boolean isStaff() { return isStaff; }

  @JsonProperty("isStaff")
  public void setStaff(boolean staff) { this.isStaff = staff; }

  @JsonProperty("superUser")
  public boolean isSuperUser() { return superUser; }

  @JsonProperty("superUser")
  public void setSuperUser(boolean superUser) { this.superUser = superUser; }

  private String tel;

  // 住所（最大100文字）
  @Size(max = 100)
  private String address;

  // 最終ログイン時刻
  @Column(name = "last_login")
  private LocalDateTime lastLogin;

  // 作成時刻
  @Column(name = "create_time")
  private LocalDateTime createTime = LocalDateTime.now();
}
