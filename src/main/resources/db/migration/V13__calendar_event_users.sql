-- V13__calendar_event_users.sql
-- Pievieno izveidotāju un uzaicinātās personas kalendāra notikumiem

ALTER TABLE calendar_event ADD COLUMN created_by VARCHAR(255) DEFAULT 'Sistēma' NOT NULL;
ALTER TABLE calendar_event ADD COLUMN invited_persons JSONB;