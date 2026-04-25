package com.ems.chat.services.rag;

import com.ems.chat.client.IncidentClient;
import com.ems.chat.dto.suggestion.IncidentSimilaireDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class RagServiceImpl implements RagService {
    private final IncidentClient incidentClient;

    public RagServiceImpl(IncidentClient incidentClient) {
        this.incidentClient = incidentClient;
    }

    @Override
    public List<IncidentSimilaireDTO> findSimilaires(String message) {
        return incidentClient.findSimilaires(message);
    }

    @Override
    public String formatForPrompt(List<IncidentSimilaireDTO> incidents) {
        if (incidents.isEmpty()) return "Aucun incident similaire trouvé.";

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < incidents.size(); i++) {
            IncidentSimilaireDTO incident = incidents.get(i);
            long joursEcoules = ChronoUnit.DAYS.between(incident.dateCreation(), LocalDateTime.now());

            sb.append("Incident #").append(i + 1).append("\n")
                    .append("Problème : ").append(incident.probleme()).append("\n")
                    .append("Solution : ").append(incident.solution()).append("\n")
                    .append("A fonctionné pour : ").append(incident.nombreUtilisateurs()).append(" utilisateurs\n")
                    .append("Il y a : ").append(joursEcoules).append(" jours\n\n");
        }
        return sb.toString();
    }
}