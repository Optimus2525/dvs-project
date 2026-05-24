-- V3__seed_data.sql
-- Ievietojam testa datus mūsu dinamiskajam dzinējam

-- 1. Izveidojam sarakstu (pieņemam, ka ID būs 1)
INSERT INTO document_list (code, name, description)
VALUES ('IEN-2026', 'Ienākošā korespondence 2026', 'Testa saraksts');

-- 2. Definējam dinamiskos laukus šim sarakstam
-- Sūtītājs (Teksts)
INSERT INTO field_definition (document_list_id, name, type, options, show_on_dashboard)
VALUES (1, 'Dokumenta sūtītājs', 'TEXT', NULL, TRUE);

-- Datums (Datums)
INSERT INTO field_definition (document_list_id, name, type, options, show_on_dashboard)
VALUES (1, 'Saņemšanas datums', 'DATE', NULL, TRUE);

-- Veids (Izvēlne). Opcijas glabājam JSONB formātā kā "values" masīvu, lai Java to nolasa kā Map<String, Object>
INSERT INTO field_definition (document_list_id, name, type, options, show_on_dashboard)
VALUES (1, 'Saņemšanas veids', 'SELECT', '{"values": ["E-pasts", "Pasts", "Kurjers", "Personīgi"]}'::jsonb, TRUE);