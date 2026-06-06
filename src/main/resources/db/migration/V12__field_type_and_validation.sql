-- V12__field_type_and_validation.sql
-- Pievieno obligātuma pazīmi dinamiskajiem laukiem, lai varētu veikt validāciju

ALTER TABLE field_definition ADD COLUMN is_required BOOLEAN DEFAULT FALSE NOT NULL;