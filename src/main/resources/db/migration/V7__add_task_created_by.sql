-- V7__add_task_created_by.sql
-- Pievieno kolonnu, kas fiksē uzdevuma izveidotāju

ALTER TABLE task ADD COLUMN created_by VARCHAR(255) DEFAULT 'Sistēma' NOT NULL;