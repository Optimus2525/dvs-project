-- V2__sync_schema_with_entities.sql
-- Sinhronizē datubāzes shēmu ar Java entītijām

-- Pievieno trūkstošo kolonnu document_card tabulai
ALTER TABLE document_card ADD COLUMN document_number VARCHAR(255);

-- Pievieno trūkstošās kolonnas document_list tabulai
ALTER TABLE document_list ADD COLUMN code VARCHAR(255);
ALTER TABLE document_list ADD COLUMN description VARCHAR(255);

-- Tā kā tabula šobrīd ir tukša, kodam var droši uzlikt NOT NULL un UNIQUE ierobežojumus
ALTER TABLE document_list ALTER COLUMN code SET NOT NULL;
ALTER TABLE document_list ADD CONSTRAINT uk_document_list_code UNIQUE (code);

-- Pārsauc kolonnas field_definition tabulā, lai tās precīzi sakristu ar entītijas laukiem
ALTER TABLE field_definition RENAME COLUMN field_name TO name;
ALTER TABLE field_definition RENAME COLUMN field_type TO type;