-- ==========================================
-- V7__seed_users.sql
-- 初期データ登録（is_staff対応）
-- ==========================================

-- パスワード例: "1234" の bcrypt ハッシュ
-- $2a$10$aKzb46XruGyc.oYf9Id1bOKYptk5Pvvp4h1I3tMQzbegRkqJTuE3O

INSERT INTO users (user_id, name, password, is_staff, super_user, last_login, tel, address, create_time)
VALUES 
('admin@test.com', '管理者', '$2a$10$aKzb46XruGyc.oYf9Id1bOKYptk5Pvvp4h1I3tMQzbegRkqJTuE3O', TRUE,  TRUE,  CURRENT_TIMESTAMP, '010-0000-0000', 'Seoul', NOW()),
('user@test.com',  '一般ユーザ', '$2a$10$aKzb46XruGyc.oYf9Id1bOKYptk5Pvvp4h1I3tMQzbegRkqJTuE3O', FALSE, FALSE, CURRENT_TIMESTAMP, '010-1111-1111', 'Busan', NOW());

-- ==========================================
-- user_roles は不要になったため削除済み
-- ==========================================
