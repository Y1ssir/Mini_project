package com.ems.chat.dto.incident;

import java.util.UUID;

public record IncidentCreatedResponseDTO(
        UUID incidentId,
        String reference,   // ex: INC-2026-1234
        String priorite,
        String statut
) {}
