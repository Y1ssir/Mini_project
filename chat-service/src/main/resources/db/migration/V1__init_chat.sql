CREATE TABLE chat_conversations (
    id UUID PRIMARY KEY,
    utilisateur_id UUID NOT NULL,
    date_creation TIMESTAMP NOT NULL,
    statut VARCHAR(50) NOT NULL,
    probleme_resume VARCHAR(1000)
);

CREATE TABLE chat_messages (
    id UUID PRIMARY KEY,
    conversation_id UUID NOT NULL REFERENCES chat_conversations(id),
    expediteur VARCHAR(50) NOT NULL,
    message TEXT NOT NULL,
    timestamp TIMESTAMP NOT NULL
);

CREATE TABLE suggestions (
    id UUID PRIMARY KEY,
    conversation_id UUID NOT NULL REFERENCES chat_conversations(id),
    incident_similaire_id UUID NOT NULL,
    score_similarite FLOAT NOT NULL,
    accepte BOOLEAN NOT NULL
);
