-- V5__complex_tasks_and_notifications.sql
-- Pievieno atbalstu Kompleksajiem uzdevumiem un Popover paziņojumiem

-- 1. Papildina Task tabulu ar uzdevuma tipu
ALTER TABLE task ADD COLUMN task_type VARCHAR(50) DEFAULT 'REGULAR' NOT NULL;

-- 2. Pārveido SubTask tabulu atbilstoši kompleksā uzdevuma prasībām
-- Dzēš veco kolonnu
ALTER TABLE sub_task DROP COLUMN completed;

-- Pievieno jaunās kolonnas
ALTER TABLE sub_task ADD COLUMN description TEXT;
ALTER TABLE sub_task ADD COLUMN assignee VARCHAR(255);
ALTER TABLE sub_task ADD COLUMN due_date DATE;
ALTER TABLE sub_task ADD COLUMN status VARCHAR(100) DEFAULT 'Nav sākts' NOT NULL;
ALTER TABLE sub_task ADD COLUMN order_index INT DEFAULT 1 NOT NULL;
ALTER TABLE sub_task ADD COLUMN is_active BOOLEAN DEFAULT FALSE NOT NULL;

-- 3. Izveido Paziņojumu (Notification) tabulu
CREATE TABLE notification (
                              id BIGSERIAL PRIMARY KEY,
                              recipient VARCHAR(255) NOT NULL,
                              message TEXT NOT NULL,
                              task_id BIGINT REFERENCES task(id) ON DELETE CASCADE,
                              is_read BOOLEAN DEFAULT FALSE NOT NULL,
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indeksi ātrdarbībai
CREATE INDEX idx_notification_recipient ON notification(recipient);
CREATE INDEX idx_sub_task_assignee ON sub_task(assignee);