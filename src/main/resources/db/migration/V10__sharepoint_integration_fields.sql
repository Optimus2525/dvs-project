-- V10__sharepoint_integration_fields.sql
-- Pievieno SharePoint identifikatorus Two-Way sinhronizācijai

-- 1. Sarakstu līmenī (Kur šis saraksts atrodas SharePointā?)
ALTER TABLE document_list ADD COLUMN sharepoint_site_id VARCHAR(255);
ALTER TABLE document_list ADD COLUMN sharepoint_list_id VARCHAR(255);

-- 2. Lauku līmenī (Kā šī kolonna saucas SharePointā?)
ALTER TABLE field_definition ADD COLUMN sharepoint_internal_name VARCHAR(255);

-- 3. Kartīšu līmenī (Kurš saraksta ieraksts (List Item) šis ir?)
ALTER TABLE document_card ADD COLUMN sharepoint_item_id VARCHAR(255);

-- Indekss ātrākai sinhronizācijai un meklēšanai
CREATE INDEX idx_document_card_sp_item_id ON document_card(sharepoint_item_id);