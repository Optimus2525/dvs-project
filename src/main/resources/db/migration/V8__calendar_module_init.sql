-- V8__calendar_module_init.sql
-- Kalendāra moduļa bāzes tabulas (SharePoint spogulis)

-- 1. Kalendāra kategorijas (Atvaļinājumi, Sapulces utt.) un to krāsas
CREATE TABLE calendar_category (
                                   id BIGSERIAL PRIMARY KEY,
                                   name VARCHAR(255) NOT NULL UNIQUE,
                                   color_code VARCHAR(50) NOT NULL DEFAULT '#0d6efd', -- CSS krāsas kods (piem., Bootstrap primārā krāsa)
                                   is_active BOOLEAN NOT NULL DEFAULT TRUE
);

-- Ievietojam noklusējuma kategorijas, kas atbilst maketam
INSERT INTO calendar_category (name, color_code) VALUES ('Ballītes', '#e8c4e9');
INSERT INTO calendar_category (name, color_code) VALUES ('Sapulces', '#71c0e6');
INSERT INTO calendar_category (name, color_code) VALUES ('Atvaļinājumi', '#f8cbad');
INSERT INTO calendar_category (name, color_code) VALUES ('Tikšanās', '#a9d18e');
INSERT INTO calendar_category (name, color_code) VALUES ('Māju sapulces', '#5b9bd5');

-- 2. Kalendāra notikumu spoguļtabula (Mirror no SharePoint)
CREATE TABLE calendar_event (
                                id BIGSERIAL PRIMARY KEY,
                                sharepoint_item_id VARCHAR(255) UNIQUE, -- ID no Microsoft Graph API
                                title VARCHAR(255) NOT NULL,
                                description TEXT,
                                start_time TIMESTAMP NOT NULL,
                                end_time TIMESTAMP NOT NULL,
                                is_all_day BOOLEAN NOT NULL DEFAULT FALSE,
                                category_id BIGINT REFERENCES calendar_category(id) ON DELETE SET NULL,
                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indeksi ātrākai datu atlasīšanai pa mēnešiem un Graph API sinhronizācijai
CREATE INDEX idx_calendar_event_dates ON calendar_event(start_time, end_time);
CREATE INDEX idx_calendar_event_sp_id ON calendar_event(sharepoint_item_id);