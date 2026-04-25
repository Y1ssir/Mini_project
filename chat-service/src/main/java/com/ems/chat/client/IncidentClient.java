package com.ems.chat.client;

import com.ems.chat.dto.suggestion.IncidentSimilaireDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class IncidentClient {

    // ✅ Mock — à remplacer par WebClient quand Service Incident est prêt
    public List<IncidentSimilaireDTO> findSimilaires(String message) {
        return List.of(
                new IncidentSimilaireDTO(
                        UUID.randomUUID(),
                        "Imprimante HP bloquée",
                        "Redémarrer le spooler d'impression",
                        8,
                        LocalDateTime.now().minusDays(3),
                        0.95f
                ),
                new IncidentSimilaireDTO(
                        UUID.randomUUID(),
                        "Imprimante ne répond pas",
                        "Réinstaller les pilotes",
                        5,
                        LocalDateTime.now().minusWeeks(1),
                        0.87f
                )
        );
    }
}