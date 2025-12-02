-- 注文ステータス列の追加（既存データは ORDERED とする）
ALTER TABLE orders
ADD COLUMN status VARCHAR(20) DEFAULT 'ORDERED';
