-- V4__task_module_init.sql
-- Uzdevumu (Workflow) moduļa datubāzes shēmas izveide

CREATE TABLE task (
                      id BIGSERIAL PRIMARY KEY,
                      title VARCHAR(255) NOT NULL,
                      start_date DATE NOT NULL,
                      due_date DATE NOT NULL,
                      assignee VARCHAR(255) NOT NULL,
                      followers JSONB, -- Masīvs ar lietotāju identifikatoriem, kas seko līdzi
                      description TEXT NOT NULL,
                      priority VARCHAR(50) NOT NULL DEFAULT 'NORMAL',
                      status VARCHAR(100) NOT NULL DEFAULT 'Nav sākts',
                      document_card_id BIGINT REFERENCES document_card(id) ON DELETE SET NULL,
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE sub_task (
                          id BIGSERIAL PRIMARY KEY,
                          title VARCHAR(255) NOT NULL,
                          completed BOOLEAN NOT NULL DEFAULT FALSE,
                          parent_task_id BIGINT NOT NULL REFERENCES task(id) ON DELETE CASCADE
);

-- Indeksi ātrākai datu atlasīšanai, meklējot pēc atbildīgās personas vai saistītā dokumenta
CREATE INDEX idx_task_assignee ON task(assignee);
CREATE INDEX idx_task_document_card_id ON task(document_card_id);