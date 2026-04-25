package com.ems.chat.dto.suggestion;

import java.time.LocalDateTime;
import java.util.UUID;

public record IncidentSimilaireDTO(
        UUID id,
        String probleme,
        String solution,
        int nombreUtilisateurs,
        LocalDateTime dateCreation,
        float scoreSimilarite
) {}