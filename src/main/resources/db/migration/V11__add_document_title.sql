-- V11__add_document_title.sql
-- Pievieno 'title' (Virsraksts) kolonnu dokumenta kartītei

ALTER TABLE document_card ADD COLUMN title VARCHAR(500) DEFAULT 'Bez virsraksta' NOT NULL;