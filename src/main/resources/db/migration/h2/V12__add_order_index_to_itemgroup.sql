-- 並び順カラムを追加（既存機能への影響なし）
ALTER TABLE itemgroup ADD COLUMN order_index INT NOT NULL DEFAULT 0;
