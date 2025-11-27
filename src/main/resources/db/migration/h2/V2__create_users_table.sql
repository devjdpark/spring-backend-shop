-- ==========================================
-- V2__create_users.sql
-- ユーザテーブル作成（is_staff対応版）
-- ==========================================

CREATE TABLE IF NOT EXISTS users (
  id BIGSERIAL PRIMARY KEY,                           -- PK
  user_id     VARCHAR(255) NOT NULL UNIQUE,            -- ログインID（メールアドレス）
  name        VARCHAR(255),                            -- ユーザ名
  password    VARCHAR(255) NOT NULL,                   -- パスワード（ハッシュ）
  is_staff    BOOLEAN DEFAULT FALSE NOT NULL,          -- 管理者権限（true=管理者, false=一般）
  super_user  BOOLEAN DEFAULT FALSE NOT NULL,          -- システム全体管理者
  last_login  TIMESTAMP,                               -- 最終ログイン時刻
  tel         VARCHAR(50),                             -- 電話番号
  address     VARCHAR(255),                            -- 住所
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP      -- 作成時刻
);

-- ==========================================
-- インデックス作成
-- ==========================================
CREATE INDEX IF NOT EXISTS idx_users_user_id ON users(user_id);

-- ==========================================
-- H2/PostgreSQL両対応の注意
-- ==========================================
-- BIGSERIAL は PostgreSQL用
-- H2では BIGSERIAL が自動的に IDENTITY として扱われます
-- よって両方のDBで動作可能です。
