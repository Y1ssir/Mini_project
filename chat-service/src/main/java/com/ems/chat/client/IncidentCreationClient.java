package com.ems.chat.client;

import com.ems.chat.dto.incident.IncidentCreateRequestDTO;
import com.ems.chat.dto.incident.IncidentCreatedResponseDTO;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class IncidentCreationClient {

    // ✅ Mock — à remplacer par WebClient vers le Service Incident quand il sera prêt
    public IncidentCreatedResponseDTO createIncident(IncidentCreateRequestDTO request) {
        UUID fakeId = UUID.randomUUID();
        int randomNum = (int) (Math.random() * 9000 + 1000);
        String reference = "INC-2026-" + randomNum;

        return new IncidentCreatedResponseDTO(
                fakeId,
                reference,
                request.priorite(),
                "Nouveau"
        );
    }
}
