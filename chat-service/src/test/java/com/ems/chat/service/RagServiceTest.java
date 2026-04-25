package com.ems.chat.service;

import com.ems.chat.dto.suggestion.IncidentSimilaireDTO;
import com.ems.chat.services.rag.RagServiceImpl;
import com.ems.chat.client.IncidentClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RagServiceTest {

    @Mock
    private IncidentClient incidentClient;

    @InjectMocks
    private RagServiceImpl ragService;

    // ─── findSimilaires ───────────────────────────────────────────────

    @Test
    void findSimilaires_shouldDelegateToClient() {
        List<IncidentSimilaireDTO> mockIncidents = List.of(
                new IncidentSimilaireDTO(UUID.randomUUID(), "PC lent", "Redémarrer", 3, LocalDateTime.now(), 0.9f)
        );
        when(incidentClient.findSimilaires("Mon PC est lent")).thenReturn(mockIncidents);

        List<IncidentSimilaireDTO> result = ragService.findSimilaires("Mon PC est lent");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).probleme()).isEqualTo("PC lent");
    }

    // ─── formatForPrompt ──────────────────────────────────────────────

    @Test
    void formatForPrompt_shouldReturnNoIncidentMessage_whenEmpty() {
        String result = ragService.formatForPrompt(List.of());

        assertThat(result).isEqualTo("Aucun incident similaire trouvé.");
    }

    @Test
    void formatForPrompt_shouldFormatCorrectly_withIncidents() {
        LocalDateTime date = LocalDateTime.now().minusDays(5);
        List<IncidentSimilaireDTO> incidents = List.of(
                new IncidentSimilaireDTO(UUID.randomUUID(), "Imprimante bloquée", "Redémarrer le spooler", 8, date, 0.95f)
        );

        String result = ragService.formatForPrompt(incidents);

        assertThat(result).contains("Incident #1");
        assertThat(result).contains("Imprimante bloquée");
        assertThat(result).contains("Redémarrer le spooler");
        assertThat(result).contains("8 utilisateurs");
        // la date est il y a 5 jours
        assertThat(result).contains("5 jours");
    }

    @Test
    void formatForPrompt_shouldNumberIncidentsCorrectly_withMultiple() {
        List<IncidentSimilaireDTO> incidents = List.of(
                new IncidentSimilaireDTO(UUID.randomUUID(), "Prob A", "Sol A", 1, LocalDateTime.now().minusDays(1), 0.9f),
                new IncidentSimilaireDTO(UUID.randomUUID(), "Prob B", "Sol B", 2, LocalDateTime.now().minusDays(2), 0.8f)
        );

        String result = ragService.formatForPrompt(incidents);

        assertThat(result).contains("Incident #1");
        assertThat(result).contains("Incident #2");
        assertThat(result).contains("Prob A");
        assertThat(result).contains("Prob B");
    }
}
