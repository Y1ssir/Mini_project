-- V1__init_comment.sql (dans comment-service/src/main/resources/db/migration/)
CREATE TABLE IF NOT EXISTS comments (
    id             BIGSERIAL PRIMARY KEY,
    incident_id    BIGINT        NOT NULL,
    content        TEXT          NOT NULL,
    author_id      VARCHAR(255)  NOT NULL,
    author_name    VARCHAR(255),
    comment_type   VARCHAR(50)   NOT NULL DEFAULT 'CLIENT',
    attachment_url TEXT,
    created_at     TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMP     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_comments_incident_id ON comments(incident_id);
CREATE INDEX idx_comments_author_id   ON comments(author_id);
