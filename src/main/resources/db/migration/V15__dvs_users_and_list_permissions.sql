-- V15__dvs_users_and_list_permissions.sql
-- Izveido lokālo DVS lietotāju bāzi un sarakstu privilēģiju sistēmu

-- 1. Lokālā DVS lietotāju tabula
CREATE TABLE dvs_user (
                          id BIGSERIAL PRIMARY KEY,
                          username VARCHAR(255) NOT NULL UNIQUE, -- Sakrīt ar Entra ID 'name'
                          email VARCHAR(255),
                          is_active BOOLEAN DEFAULT TRUE NOT NULL,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Ievietojam galveno Administratoru, lai sistēma nebloķētu autoru
INSERT INTO dvs_user (username, is_active) VALUES ('Ilmārs Mednis', TRUE);

-- 2. Sarakstu privilēģiju tabula
CREATE TABLE list_permission (
                                 id BIGSERIAL PRIMARY KEY,
                                 dvs_user_id BIGINT NOT NULL REFERENCES dvs_user(id) ON DELETE CASCADE,
                                 document_list_id BIGINT NOT NULL REFERENCES document_list(id) ON DELETE CASCADE,
                                 permission_level VARCHAR(50) NOT NULL, -- 'READ_ONLY' vai 'EDITOR'
                                 UNIQUE(dvs_user_id, document_list_id)
);