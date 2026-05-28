-- V6__ui_settings_init.sql
-- Izveido tabulu sistēmas vizuālo iestatījumu glabāšanai (CSS mainīgie)

CREATE TABLE ui_setting (
                            setting_key VARCHAR(50) PRIMARY KEY,
                            setting_value VARCHAR(255) NOT NULL
);

-- Noklusējuma vērtības kompaktam UI
INSERT INTO ui_setting (setting_key, setting_value) VALUES ('--dvs-font-size', '14px');
INSERT INTO ui_setting (setting_key, setting_value) VALUES ('--dvs-card-padding', '0.75rem');
INSERT INTO ui_setting (setting_key, setting_value) VALUES ('--dvs-table-padding', '0.5rem');