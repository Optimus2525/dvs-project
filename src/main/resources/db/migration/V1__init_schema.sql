-- V1__init_schema.sql
-- Mūsu DVS arhitektūras pamats

-- 1. Dokumentu Saraksti (piem., "Ienākošie 2026")
CREATE TABLE document_list (
                               id BIGSERIAL PRIMARY KEY,
                               name VARCHAR(255) NOT NULL,
                               template_id BIGINT,
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Tehniskie saraksti / Kolonnu definīcijas (Dinamiskie metadati)
CREATE TABLE field_definition (
                                  id BIGSERIAL PRIMARY KEY,
                                  document_list_id BIGINT NOT NULL REFERENCES document_list(id) ON DELETE CASCADE,
                                  field_name VARCHAR(255) NOT NULL,
                                  field_type VARCHAR(50) NOT NULL,
                                  options JSONB, -- Šeit glabāsim Dropdown vērtības, ja tādas būs
                                  show_on_dashboard BOOLEAN DEFAULT TRUE
);

-- 3. Dokumentu Kartītes (Pati svarīgākā tabula ar JSONB)
CREATE TABLE document_card (
                               id BIGSERIAL PRIMARY KEY,
                               document_list_id BIGINT NOT NULL REFERENCES document_list(id) ON DELETE CASCADE,
                               created_by VARCHAR(255) NOT NULL,
                               sharepoint_file_id VARCHAR(255),
                               metadata JSONB, -- Šeit dzīvos visi dinamiskie lauki!
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);