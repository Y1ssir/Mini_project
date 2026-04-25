package com.ems.chat.service;

import com.ems.chat.client.IncidentCreationClient;
import com.ems.chat.dto.conversation.ConversationRequestDTO;
import com.ems.chat.dto.conversation.ConversationResponseDTO;
import com.ems.chat.dto.incident.IncidentCreateRequestDTO;
import com.ems.chat.dto.incident.IncidentCreatedResponseDTO;
import com.ems.chat.entity.ChatConversation;
import com.ems.chat.mapper.conversation.ConversationMapper;
import com.ems.chat.repository.ChatConversationRepo;
import com.ems.chat.services.conversation.ConversationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConversationServiceTest {

    @Mock
    private ChatConversationRepo conversationRepo;

    @Mock
    private ConversationMapper conversationMapper;

    @Mock
    private IncidentCreationClient incidentCreationClient;

    @InjectMocks
    private ConversationServiceImpl conversationService;

    private ChatConversation conversation;
    private ConversationResponseDTO responseDTO;
    private UUID conversationId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        conversationId = UUID.randomUUID();
        userId = UUID.randomUUID();

        conversation = new ChatConversation();
        conversation.setUtilisateurId(userId);

        responseDTO = new ConversationResponseDTO(
                ChatConversation.Statutconversation.EN_COURS,
                null,
                LocalDateTime.now(),
                conversationId
        );
    }

    // ─── createConversation ───────────────────────────────────────────

    @Test
    void createConversation_shouldSaveAndReturnDTO() {
        ConversationRequestDTO request = new ConversationRequestDTO(userId);

        when(conversationMapper.toEntity(request)).thenReturn(conversation);
        when(conversationRepo.save(conversation)).thenReturn(conversation);
        when(conversationMapper.toDTO(conversation)).thenReturn(responseDTO);

        ConversationResponseDTO result = conversationService.createConversation(request);

        assertThat(result).isNotNull();
        assertThat(result.statut()).isEqualTo(ChatConversation.Statutconversation.EN_COURS);
        verify(conversationRepo, times(1)).save(conversation);
    }

    // ─── GetConversation ──────────────────────────────────────────────

    @Test
    void getConversation_shouldReturnDTO_whenFound() {
        when(conversationRepo.findById(conversationId)).thenReturn(Optional.of(conversation));
        when(conversationMapper.toDTO(conversation)).thenReturn(responseDTO);

        ConversationResponseDTO result = conversationService.GetConversation(conversationId);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(conversationId);
    }

    @Test
    void getConversation_shouldThrow_whenNotFound() {
        when(conversationRepo.findById(conversationId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> conversationService.GetConversation(conversationId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Conversation introuvable");
    }

    // ─── updateStatus ─────────────────────────────────────────────────

    @Test
    void updateStatus_shouldChangeStatutAndSave() {
        when(conversationRepo.findById(conversationId)).thenReturn(Optional.of(conversation));
        ConversationResponseDTO resolvedDTO = new ConversationResponseDTO(
                ChatConversation.Statutconversation.RESOLU, null, LocalDateTime.now(), conversationId);
        when(conversationMapper.toDTO(conversation)).thenReturn(resolvedDTO);

        ConversationResponseDTO result = conversationService.updateStatus(
                conversationId, ChatConversation.Statutconversation.RESOLU);

        assertThat(conversation.getStatut()).isEqualTo(ChatConversation.Statutconversation.RESOLU);
        assertThat(result.statut()).isEqualTo(ChatConversation.Statutconversation.RESOLU);
        verify(conversationRepo).save(conversation);
    }

    // ─── updateProblemResume ──────────────────────────────────────────

    @Test
    void updateProblemResume_shouldUpdateAndSave() {
        String resume = "Problème réseau";
        when(conversationRepo.findById(conversationId)).thenReturn(Optional.of(conversation));
        ConversationResponseDTO updatedDTO = new ConversationResponseDTO(
                ChatConversation.Statutconversation.EN_COURS, resume, LocalDateTime.now(), conversationId);
        when(conversationMapper.toDTO(conversation)).thenReturn(updatedDTO);

        ConversationResponseDTO result = conversationService.updateProblemResume(conversationId, resume);

        assertThat(conversation.getProblemeResume()).isEqualTo(resume);
        assertThat(result.problemeResume()).isEqualTo(resume);
        verify(conversationRepo).save(conversation);
    }

    // ─── createIncident ───────────────────────────────────────────────

    @Test
    void createIncident_shouldCallClientAndSetStatutIncidentCree() {
        conversation.setProblemeResume("Imprimante bloquée");
        when(conversationRepo.findById(conversationId)).thenReturn(Optional.of(conversation));

        UUID incidentId = UUID.randomUUID();
        IncidentCreatedResponseDTO fakeIncident = new IncidentCreatedResponseDTO(
                incidentId, "INC-2026-4567", "Moyenne", "Nouveau");
        when(incidentCreationClient.createIncident(any(IncidentCreateRequestDTO.class)))
                .thenReturn(fakeIncident);

        IncidentCreatedResponseDTO result = conversationService.createIncident(conversationId);

        // Le statut doit passer à INCIDENT_CREE
        assertThat(conversation.getStatut()).isEqualTo(ChatConversation.Statutconversation.INCIDENT_CREE);
        // La réponse contient la référence de l'incident
        assertThat(result.reference()).isEqualTo("INC-2026-4567");
        assertThat(result.incidentId()).isEqualTo(incidentId);
        verify(conversationRepo).save(conversation);
        verify(incidentCreationClient).createIncident(any());
    }

    @Test
    void createIncident_shouldUseFallbackTitle_whenProblemeResumeIsNull() {
        // problemeResume = null → titre de fallback
        when(conversationRepo.findById(conversationId)).thenReturn(Optional.of(conversation));
        when(incidentCreationClient.createIncident(any())).thenReturn(
                new IncidentCreatedResponseDTO(UUID.randomUUID(), "INC-2026-0001", "Moyenne", "Nouveau")
        );

        conversationService.createIncident(conversationId);

        verify(incidentCreationClient).createIncident(argThat(req ->
                req.titre().equals("Incident créé depuis le chatbot")
        ));
    }

    @Test
    void createIncident_shouldThrow_whenConversationNotFound() {
        when(conversationRepo.findById(conversationId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> conversationService.createIncident(conversationId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Conversation introuvable");
    }
}

