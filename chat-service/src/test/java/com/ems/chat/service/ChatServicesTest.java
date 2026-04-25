package com.ems.chat.service;

import com.ems.chat.dto.message.ChatRequestDTO;
import com.ems.chat.dto.message.ChatResponseDTO;
import com.ems.chat.dto.suggestion.IncidentSimilaireDTO;
import com.ems.chat.entity.ChatConversation;
import com.ems.chat.entity.ChatMessages;
import com.ems.chat.mapper.message.ChatMapper;
import com.ems.chat.repository.ChatConversationRepo;
import com.ems.chat.repository.ChatMessagesRepo;
import com.ems.chat.services.message.ChatServicesImpl;
import com.ems.chat.services.rag.RagService;
import com.ems.chat.services.suggestion.SuggestionsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServicesTest {

    @Mock
    private ChatMessagesRepo chatRepo;

    @Mock
    private ChatConversationRepo conversationRepo;

    @Mock
    private ChatMapper chatMapper;

    @Mock
    private RagService ragService;

    @Mock
    private SuggestionsService suggestionsService;

    // ChatClient.Builder est construit dans le constructeur → on le mock manuellement
    @Mock
    private ChatClient.Builder chatClientBuilder;

    @Mock
    private ChatClient chatClient;

    @InjectMocks
    private ChatServicesImpl chatServices;

    private UUID conversationId;
    private UUID messageId;
    private ChatConversation conversation;

    @BeforeEach
    void setUp() {
        conversationId = UUID.randomUUID();
        messageId = UUID.randomUUID();
        conversation = new ChatConversation();

        // Le Builder retourne le client mocké
        when(chatClientBuilder.build()).thenReturn(chatClient);

        // Recréer l'instance avec le builder mocké (InjectMocks ne gère pas ce cas)
        chatServices = new ChatServicesImpl(
                chatRepo, conversationRepo, chatMapper, chatClientBuilder, ragService, suggestionsService
        );
    }

    // ─── GetHistory ───────────────────────────────────────────────────

    @Test
    void getHistory_shouldReturnMappedDTOs() {
        ChatMessages msg1 = new ChatMessages();
        msg1.setMessage("Bonjour");
        msg1.setExpediteur(ChatMessages.Speaker.USER);

        ChatMessages msg2 = new ChatMessages();
        msg2.setMessage("Je peux vous aider.");
        msg2.setExpediteur(ChatMessages.Speaker.BOT);

        List<ChatMessages> messages = List.of(msg1, msg2);

        List<ChatResponseDTO> expectedDTOs = List.of(
                new ChatResponseDTO(ChatResponseDTO.FluxStatut.END, "Bonjour", "USER", LocalDateTime.now().toString()),
                new ChatResponseDTO(ChatResponseDTO.FluxStatut.END, "Je peux vous aider.", "BOT", LocalDateTime.now().toString())
        );

        when(chatRepo.findByConversationId(conversationId)).thenReturn(messages);
        when(chatMapper.toDTOList(messages)).thenReturn(expectedDTOs);

        List<ChatResponseDTO> result = chatServices.GetHistory(conversationId);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).content()).isEqualTo("Bonjour");
        assertThat(result.get(1).content()).isEqualTo("Je peux vous aider.");
    }

    @Test
    void getHistory_shouldReturnEmptyList_whenNoMessages() {
        when(chatRepo.findByConversationId(conversationId)).thenReturn(List.of());
        when(chatMapper.toDTOList(List.of())).thenReturn(List.of());

        List<ChatResponseDTO> result = chatServices.GetHistory(conversationId);

        assertThat(result).isEmpty();
    }

    // ─── ChangeAnswer ─────────────────────────────────────────────────

    @Test
    void changeAnswer_shouldUpdateMessageAndDeleteSubsequentOnes() {
        ChatMessages targetMessage = new ChatMessages();
        targetMessage.setMessage("Ancienne réponse");
        targetMessage.setConversation(conversation);

        ChatMessages laterMessage = new ChatMessages();
        laterMessage.setMessage("Message plus récent");
        laterMessage.setConversation(conversation);

        when(chatRepo.findById(messageId)).thenReturn(Optional.of(targetMessage));
        when(chatRepo.filterByTimeStamp(any(), any())).thenReturn(List.of(laterMessage));

        ChatRequestDTO request = new ChatRequestDTO(conversationId, messageId, "Nouvelle réponse corrigée");

        StepVerifier.create(chatServices.ChangeAnswer(messageId, request))
                .verifyComplete();

        assertThat(targetMessage.getMessage()).isEqualTo("Nouvelle réponse corrigée");
        verify(chatRepo).deleteAll(List.of(laterMessage));
        verify(chatRepo).save(targetMessage);
    }

    @Test
    void changeAnswer_shouldThrow_whenMessageNotFound() {
        when(chatRepo.findById(messageId)).thenReturn(Optional.empty());
        ChatRequestDTO request = new ChatRequestDTO(conversationId, messageId, "Correction");

        StepVerifier.create(chatServices.ChangeAnswer(messageId, request))
                .expectErrorMatches(e -> e instanceof RuntimeException
                        && e.getMessage().contains("Message introuvable"))
                .verify();
    }

    @Test
    void changeAnswer_shouldNotCallDeleteAll_whenNoSubsequentMessages() {
        ChatMessages targetMessage = new ChatMessages();
        targetMessage.setMessage("Message seul");
        targetMessage.setConversation(conversation);

        when(chatRepo.findById(messageId)).thenReturn(Optional.of(targetMessage));
        when(chatRepo.filterByTimeStamp(any(), any())).thenReturn(List.of());

        ChatRequestDTO request = new ChatRequestDTO(conversationId, messageId, "Correction");

        StepVerifier.create(chatServices.ChangeAnswer(messageId, request))
                .verifyComplete();

        verify(chatRepo, never()).deleteAll(any());
        verify(chatRepo).save(targetMessage);
    }
}
