-- V1__baseline.sql
-- Bāzes arhitektūra, Enums un GIN indeksi (Squashed V1-V16)

CREATE TABLE document_list (
                               id BIGSERIAL PRIMARY KEY,
                               code VARCHAR(255) NOT NULL UNIQUE,
                               name VARCHAR(255) NOT NULL,
                               description VARCHAR(255),
                               template_id BIGINT,
                               sharepoint_site_id VARCHAR(255),
                               sharepoint_list_id VARCHAR(255),
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE field_definition (
                                  id BIGSERIAL PRIMARY KEY,
                                  document_list_id BIGINT NOT NULL REFERENCES document_list(id) ON DELETE CASCADE,
                                  name VARCHAR(255) NOT NULL,
                                  type VARCHAR(50) NOT NULL,
                                  is_required BOOLEAN DEFAULT FALSE NOT NULL,
                                  options JSONB,
                                  sharepoint_internal_name VARCHAR(255),
                                  show_on_dashboard BOOLEAN DEFAULT TRUE
);

CREATE TABLE document_card (
                               id BIGSERIAL PRIMARY KEY,
                               document_list_id BIGINT NOT NULL REFERENCES document_list(id) ON DELETE CASCADE,
                               title VARCHAR(500) DEFAULT 'Bez virsraksta' NOT NULL,
                               document_number VARCHAR(255),
                               created_by VARCHAR(255) NOT NULL,
                               sharepoint_item_id VARCHAR(255),
                               metadata JSONB,
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_document_card_sp_item_id ON document_card(sharepoint_item_id);
-- Kritiski svarīgs GIN Indekss JSONB meklēšanai!
CREATE INDEX idx_document_card_metadata_gin ON document_card USING GIN (metadata);

CREATE TABLE document_file (
                               id BIGSERIAL PRIMARY KEY,
                               document_card_id BIGINT NOT NULL REFERENCES document_card(id) ON DELETE CASCADE,
                               sharepoint_file_id VARCHAR(255),
                               file_name VARCHAR(500) NOT NULL,
                               file_size BIGINT,
                               mime_type VARCHAR(255),
                               uploaded_by VARCHAR(255) NOT NULL,
                               file_role VARCHAR(50) NOT NULL DEFAULT 'ATTACHMENT',
                               uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_document_file_card_id ON document_file(document_card_id);
CREATE INDEX idx_document_file_sp_id ON document_file(sharepoint_file_id);

CREATE TABLE task (
                      id BIGSERIAL PRIMARY KEY,
                      title VARCHAR(255) NOT NULL,
                      task_type VARCHAR(50) DEFAULT 'REGULAR' NOT NULL,
                      start_date DATE NOT NULL,
                      due_date DATE NOT NULL,
                      assignee VARCHAR(255) NOT NULL,
                      created_by VARCHAR(255) DEFAULT 'Sistēma' NOT NULL,
                      followers JSONB,
                      description TEXT NOT NULL,
                      priority VARCHAR(50) NOT NULL DEFAULT 'NORMAL',
                      status VARCHAR(100) NOT NULL DEFAULT 'NOT_STARTED',
                      document_card_id BIGINT REFERENCES document_card(id) ON DELETE SET NULL,
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_task_assignee ON task(assignee);
CREATE INDEX idx_task_document_card_id ON task(document_card_id);
-- Kritiski svarīgs GIN Indekss JSONB masīvam!
CREATE INDEX idx_task_followers_gin ON task USING GIN (followers);

CREATE TABLE sub_task (
                          id BIGSERIAL PRIMARY KEY,
                          parent_task_id BIGINT NOT NULL REFERENCES task(id) ON DELETE CASCADE,
                          title VARCHAR(255) NOT NULL,
                          description TEXT,
                          assignee VARCHAR(255),
                          due_date DATE,
                          status VARCHAR(100) DEFAULT 'NOT_STARTED' NOT NULL,
                          order_index INT DEFAULT 1 NOT NULL,
                          is_active BOOLEAN DEFAULT FALSE NOT NULL
);

CREATE INDEX idx_sub_task_assignee ON sub_task(assignee);

CREATE TABLE notification (
                              id BIGSERIAL PRIMARY KEY,
                              recipient VARCHAR(255) NOT NULL,
                              message TEXT NOT NULL,
                              task_id BIGINT REFERENCES task(id) ON DELETE CASCADE,
                              is_read BOOLEAN DEFAULT FALSE NOT NULL,
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_notification_recipient ON notification(recipient);

CREATE TABLE ui_setting (
                            setting_key VARCHAR(50) PRIMARY KEY,
                            setting_value VARCHAR(255) NOT NULL
);

CREATE TABLE calendar_category (
                                   id BIGSERIAL PRIMARY KEY,
                                   name VARCHAR(255) NOT NULL UNIQUE,
                                   color_code VARCHAR(50) NOT NULL DEFAULT '#0d6efd',
                                   is_active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE calendar_event (
                                id BIGSERIAL PRIMARY KEY,
                                sharepoint_item_id VARCHAR(255) UNIQUE,
                                title VARCHAR(255) NOT NULL,
                                description TEXT,
                                start_time TIMESTAMP NOT NULL,
                                end_time TIMESTAMP NOT NULL,
                                is_all_day BOOLEAN NOT NULL DEFAULT FALSE,
                                category_id BIGINT REFERENCES calendar_category(id) ON DELETE SET NULL,
                                created_by VARCHAR(255) DEFAULT 'Sistēma' NOT NULL,
                                invited_persons JSONB,
                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_calendar_event_dates ON calendar_event(start_time, end_time);
CREATE INDEX idx_calendar_event_sp_id ON calendar_event(sharepoint_item_id);

CREATE TABLE app_user_role (
                               id BIGSERIAL PRIMARY KEY,
                               username VARCHAR(255) NOT NULL,
                               role_name VARCHAR(50) NOT NULL,
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               UNIQUE(username, role_name)
);

CREATE TABLE dvs_user (
                          id BIGSERIAL PRIMARY KEY,
                          username VARCHAR(255) NOT NULL UNIQUE,
                          email VARCHAR(255),
                          is_active BOOLEAN DEFAULT TRUE NOT NULL,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE list_permission (
                                 id BIGSERIAL PRIMARY KEY,
                                 dvs_user_id BIGINT NOT NULL REFERENCES dvs_user(id) ON DELETE CASCADE,
                                 document_list_id BIGINT NOT NULL REFERENCES document_list(id) ON DELETE CASCADE,
                                 permission_level VARCHAR(50) NOT NULL,
                                 UNIQUE(dvs_user_id, document_list_id)
);

-- ==========================================
-- SĀKOTNĒJIE DATI (Seed Data)
-- ==========================================
INSERT INTO ui_setting (setting_key, setting_value) VALUES ('--dvs-font-size', '14px');
INSERT INTO ui_setting (setting_key, setting_value) VALUES ('--dvs-card-padding', '0.75rem');
INSERT INTO ui_setting (setting_key, setting_value) VALUES ('--dvs-table-padding', '0.5rem');

INSERT INTO calendar_category (name, color_code) VALUES ('Ballītes', '#e8c4e9');
INSERT INTO calendar_category (name, color_code) VALUES ('Sapulces', '#71c0e6');
INSERT INTO calendar_category (name, color_code) VALUES ('Atvaļinājumi', '#f8cbad');
INSERT INTO calendar_category (name, color_code) VALUES ('Tikšanās', '#a9d18e');
INSERT INTO calendar_category (name, color_code) VALUES ('Māju sapulces', '#5b9bd5');

INSERT INTO app_user_role (username, role_name) VALUES ('Ilmārs Mednis', 'ROLE_ADMIN');
INSERT INTO dvs_user (username, is_active) VALUES ('Ilmārs Mednis', TRUE);

INSERT INTO document_list (code, name, description) VALUES ('IEN-2026', 'Ienākošā korespondence 2026', 'Testa saraksts');
INSERT INTO field_definition (document_list_id, name, type, options, show_on_dashboard) VALUES (1, 'Dokumenta sūtītājs', 'TEXT', NULL, TRUE);
INSERT INTO field_definition (document_list_id, name, type, options, show_on_dashboard) VALUES (1, 'Saņemšanas datums', 'DATE', NULL, TRUE);
INSERT INTO field_definition (document_list_id, name, type, options, show_on_dashboard) VALUES (1, 'Saņemšanas veids', 'SELECT', '{"values": ["E-pasts", "Pasts", "Kurjers", "Personīgi"]}'::jsonb, TRUE);