package com.ems.chat.service;

import com.ems.chat.dto.suggestion.IncidentSimilaireDTO;
import com.ems.chat.dto.suggestion.SuggestionsDTO;
import com.ems.chat.entity.ChatConversation;
import com.ems.chat.entity.Suggestions;
import com.ems.chat.mapper.suggestion.SuggestionsMapper;
import com.ems.chat.repository.ChatConversationRepo;
import com.ems.chat.repository.SuggestionsRepo;
import com.ems.chat.services.suggestion.SuggestionsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SuggestionsServiceTest {

    @Mock
    private SuggestionsRepo suggestionsRepo;

    @Mock
    private ChatConversationRepo conversationRepo;

    @Mock
    private SuggestionsMapper suggestionsMapper;

    @InjectMocks
    private SuggestionsServiceImpl suggestionsService;

    private UUID conversationId;
    private UUID suggestionId;
    private ChatConversation conversation;
    private Suggestions suggestion;

    @BeforeEach
    void setUp() {
        conversationId = UUID.randomUUID();
        suggestionId = UUID.randomUUID();

        conversation = new ChatConversation();

        suggestion = new Suggestions();
        suggestion.setConversation(conversation);
        suggestion.setScore_similarite(0.95f);
    }

    // ─── saveSuggestions ──────────────────────────────────────────────

    @Test
    void saveSuggestions_shouldPersistAllIncidents() {
        List<IncidentSimilaireDTO> incidents = List.of(
                new IncidentSimilaireDTO(UUID.randomUUID(), "PC lent", "Redémarrer", 3, LocalDateTime.now(), 0.9f),
                new IncidentSimilaireDTO(UUID.randomUUID(), "Réseau coupé", "Vérifier câble", 5, LocalDateTime.now(), 0.85f)
        );

        when(conversationRepo.findById(conversationId)).thenReturn(Optional.of(conversation));

        suggestionsService.saveSuggestions(conversationId, incidents);

        // 2 suggestions doivent être sauvegardées
        verify(suggestionsRepo, times(1)).saveAll(argThat(list ->
                ((List<?>) list).size() == 2
        ));
    }

    @Test
    void saveSuggestions_shouldThrow_whenConversationNotFound() {
        when(conversationRepo.findById(conversationId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> suggestionsService.saveSuggestions(conversationId, List.of()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Conversation introuvable");
    }

    // ─── acceptSuggestion ─────────────────────────────────────────────

    @Test
    void acceptSuggestion_shouldMarkAcceptedAndSetStatutResolu() {
        when(suggestionsRepo.findById(suggestionId)).thenReturn(Optional.of(suggestion));

        SuggestionsDTO dto = new SuggestionsDTO(
                suggestionId, conversationId, UUID.randomUUID(), 0.95f, true);
        when(suggestionsMapper.toDTO(suggestion)).thenReturn(dto);

        SuggestionsDTO result = suggestionsService.acceptSuggestion(suggestionId);

        assertThat(suggestion.getAccepte()).isTrue();
        assertThat(conversation.getStatut()).isEqualTo(ChatConversation.Statutconversation.RESOLU);
        assertThat(result.accepte()).isTrue();

        verify(suggestionsRepo).save(suggestion);
        verify(conversationRepo).save(conversation);
    }

    @Test
    void acceptSuggestion_shouldThrow_whenSuggestionNotFound() {
        when(suggestionsRepo.findById(suggestionId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> suggestionsService.acceptSuggestion(suggestionId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Suggestion introuvable");
    }
}
