package com.ems.chat.dto.incident;

import java.util.UUID;

public record IncidentCreateRequestDTO(
        UUID utilisateurId,
        String titre,
        String description,
        String priorite
) {}
