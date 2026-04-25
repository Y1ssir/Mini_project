package com.ems.chat.services.suggestion;

import com.ems.chat.dto.suggestion.IncidentSimilaireDTO;
import com.ems.chat.dto.suggestion.SuggestionsDTO; // Import nécessaire
import java.util.List;
import java.util.UUID;

public interface SuggestionsService {
    void saveSuggestions(UUID conversationId, List<IncidentSimilaireDTO> incidents);

    // Change 'void' par 'SuggestionsDTO' ici
    SuggestionsDTO acceptSuggestion(UUID suggestionId);
}