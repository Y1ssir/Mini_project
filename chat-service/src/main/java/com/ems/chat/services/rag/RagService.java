package com.ems.chat.services.rag;

import java.util.List;
import com.ems.chat.dto.suggestion.IncidentSimilaireDTO;

public interface RagService {
    List<IncidentSimilaireDTO> findSimilaires(String message);
    String formatForPrompt(List<IncidentSimilaireDTO> incidents);
}