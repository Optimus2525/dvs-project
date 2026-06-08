-- V14__user_roles.sql
-- Izveido lokālo lomu pārvaldības tabulu un piešķir sākotnējās tiesības

CREATE TABLE app_user_role (
                               id BIGSERIAL PRIMARY KEY,
                               username VARCHAR(255) NOT NULL, -- Šeit glabāsim Entra ID vārdu (piem., 'Ilmārs Mednis')
                               role_name VARCHAR(50) NOT NULL, -- Spring Security loma (piem., 'ROLE_ADMIN')
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               UNIQUE(username, role_name)
);

-- Ievietojam pirmo un galveno Administratoru
INSERT INTO app_user_role (username, role_name) VALUES ('Ilmārs Mednis', 'ROLE_ADMIN');