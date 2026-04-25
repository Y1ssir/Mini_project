package com.ems.chat.dto.suggestion;

import java.util.UUID;

public record SuggestionsDTO(
        UUID id,
        UUID conversationId,
        UUID incidentSimilaireId,
        float scoreSimilarite,
        boolean accepte
) {}