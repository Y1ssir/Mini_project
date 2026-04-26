-- V1__init_incident.sql (dans incident-service/src/main/resources/db/migration/)
CREATE TABLE IF NOT EXISTS incidents (
    id             BIGSERIAL PRIMARY KEY,
    title          VARCHAR(255)  NOT NULL,
    description    TEXT          NOT NULL,
    status         VARCHAR(50)   NOT NULL DEFAULT 'OPEN',
    priority       VARCHAR(50)   NOT NULL DEFAULT 'MEDIUM',
    created_by     VARCHAR(255)  NOT NULL,
    assigned_to    VARCHAR(255),
    category       VARCHAR(100),
    attachment_url TEXT,
    created_at     TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMP     NOT NULL DEFAULT NOW(),
    resolved_at    TIMESTAMP
);

CREATE INDEX idx_incidents_status      ON incidents(status);
CREATE INDEX idx_incidents_created_by  ON incidents(created_by);
CREATE INDEX idx_incidents_assigned_to ON incidents(assigned_to);
CREATE INDEX idx_incidents_category    ON incidents(category);
