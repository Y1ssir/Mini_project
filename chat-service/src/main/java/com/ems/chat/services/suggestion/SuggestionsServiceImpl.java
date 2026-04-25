package com.ems.chat.services.suggestion;

import com.ems.chat.dto.suggestion.IncidentSimilaireDTO;
import com.ems.chat.dto.suggestion.SuggestionsDTO;
import com.ems.chat.entity.ChatConversation;
import com.ems.chat.entity.Suggestions;
import com.ems.chat.mapper.suggestion.SuggestionsMapper;
import com.ems.chat.repository.ChatConversationRepo;
import com.ems.chat.repository.SuggestionsRepo;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class SuggestionsServiceImpl implements SuggestionsService {

    private final SuggestionsRepo suggestionsRepo;
    private final ChatConversationRepo conversationRepo;
    private final SuggestionsMapper suggestionsMapper;

    public SuggestionsServiceImpl(
            SuggestionsRepo suggestionsRepo,
            ChatConversationRepo conversationRepo,
            SuggestionsMapper suggestionsMapper
    ) {
        this.suggestionsRepo = suggestionsRepo;
        this.conversationRepo = conversationRepo;
        this.suggestionsMapper = suggestionsMapper;
    }

    // 1. Convention de nommage : camelCase (minuscule au début)
    private ChatConversation findConversation(UUID conversationId) {
        return conversationRepo.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation introuvable avec l'ID : " + conversationId));
    }

    private Suggestions findSuggestion(UUID suggestionId) {
        return suggestionsRepo.findById(suggestionId)
                .orElseThrow(() -> new RuntimeException("Suggestion introuvable avec l'ID : " + suggestionId));
    }

    @Override
    public void saveSuggestions(UUID conversationId, List<IncidentSimilaireDTO> incidents) {
        ChatConversation conversation = findConversation(conversationId);

        // 2. Mapping direct : On évite de créer un objet intermédiaire inutile
        List<Suggestions> suggestions = incidents.stream()
                .map(incident -> {
                    Suggestions suggestion = new Suggestions();
                    suggestion.setConversation(conversation);
                    suggestion.setIncidentSimilaireId(incident.id());
                    suggestion.setScore_similarite(incident.scoreSimilarite());
                    suggestion.setAccepte(false); // Règle métier explicite
                    return suggestion;
                }).toList();

        suggestionsRepo.saveAll(suggestions);
    }

    @Override
    public SuggestionsDTO acceptSuggestion(UUID suggestionId) {
        Suggestions suggestion = findSuggestion(suggestionId);

        // 3. On applique la logique métier
        suggestion.setAccepte(true);

        ChatConversation conversation = suggestion.getConversation();
        conversation.setStatut(ChatConversation.Statutconversation.RESOLU);

        // Les sauvegardes (optionnelles grâce à @Transactional, mais bonnes pour la lisibilité)
        suggestionsRepo.save(suggestion);
        conversationRepo.save(conversation);

        // 4. On retourne le DTO mis à jour pour le Frontend
        return suggestionsMapper.toDTO(suggestion);
    }
}