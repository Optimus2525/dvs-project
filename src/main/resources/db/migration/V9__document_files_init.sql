-- V9__document_files_init.sql
-- Dokumentu failu 1:N relācijas izveide un sagatavošanās eParaksts integrācijai

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

-- Datu migrācija (ja kādai kartītei jau bija sharepoint_file_id, to pārnesam kā MAIN_DOCUMENT)
INSERT INTO document_file (document_card_id, sharepoint_file_id, file_name, uploaded_by, file_role)
SELECT id, sharepoint_file_id, 'Nezināms fails no vecās sistēmas', created_by, 'MAIN_DOCUMENT'
FROM document_card
WHERE sharepoint_file_id IS NOT NULL;

-- Dzēšam veco kolonnu no dokumenta kartītes
ALTER TABLE document_card DROP COLUMN sharepoint_file_id;