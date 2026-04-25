// ChatController.java
package com.ems.chat.controller;

import com.ems.chat.dto.conversation.ConversationRequestDTO;
import com.ems.chat.dto.conversation.ConversationResponseDTO;
import com.ems.chat.dto.incident.IncidentCreatedResponseDTO;
import com.ems.chat.dto.message.ChatRequestDTO;
import com.ems.chat.dto.message.ChatResponseDTO;
import com.ems.chat.dto.suggestion.SuggestionsDTO;
import com.ems.chat.entity.ChatConversation;
import com.ems.chat.services.conversation.ConversationService;
import com.ems.chat.services.message.ChatServices;
import com.ems.chat.services.suggestion.SuggestionsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    private final ChatServices chatServices;
    private final ConversationService conversationService;
    private final SuggestionsService suggestionsService;

    public ChatController(
            ChatServices chatServices,
            ConversationService conversationService,
            SuggestionsService suggestionsService
    ) {
        this.chatServices = chatServices;
        this.conversationService = conversationService;
        this.suggestionsService = suggestionsService;
    }

    // ─── Conversation ─────────────────────────────────────────────────
    @PostMapping("/conversation")
    public ResponseEntity<ConversationResponseDTO> createConversation(@RequestBody ConversationRequestDTO request) {
        return ResponseEntity.ok(conversationService.createConversation(request));
    }

    @GetMapping("/conversation/{conversationId}")
    public ResponseEntity<ConversationResponseDTO> getConversation(@PathVariable UUID conversationId) {
        return ResponseEntity.ok(conversationService.GetConversation(conversationId));
    }

    @PatchMapping("/conversation/{conversationId}/status")
    public ResponseEntity<ConversationResponseDTO> updateStatus(
            @PathVariable UUID conversationId,
            @RequestParam ChatConversation.Statutconversation statut
    ) {
        return ResponseEntity.ok(conversationService.updateStatus(conversationId, statut));
    }

    @PatchMapping("/conversation/{conversationId}/probleme")
    public ResponseEntity<ConversationResponseDTO> updateProblemeResume(
            @PathVariable UUID conversationId,
            @RequestParam String problemeResume
    ) {
        return ResponseEntity.ok(conversationService.updateProblemResume(conversationId, problemeResume));
    }

    /**
     * Déclenché quand l'utilisateur confirme qu'aucune solution ne fonctionne.
     * Crée un incident dans le Service Incident et passe le statut à INCIDENT_CRÉÉ.
     */
    @PostMapping("/conversation/{conversationId}/create-incident")
    public ResponseEntity<IncidentCreatedResponseDTO> createIncident(@PathVariable UUID conversationId) {
        return ResponseEntity.ok(conversationService.createIncident(conversationId));
    }

    // ─── Message (REST uniquement — le streaming est via WebSocket) ───
    @GetMapping("/conversation/{conversationId}/history")
    public ResponseEntity<List<ChatResponseDTO>> history(@PathVariable UUID conversationId) {
        return ResponseEntity.ok(chatServices.GetHistory(conversationId));
    }

    @PutMapping("/message/{messageId}")
    public ResponseEntity<Void> changeAnswer(
            @PathVariable UUID messageId,
            @RequestBody ChatRequestDTO request
    ) {
        chatServices.ChangeAnswer(messageId, request).block();
        return ResponseEntity.ok().build();
    }

    // ─── Suggestions ──────────────────────────────────────────────────
    @PatchMapping("/suggestion/{suggestionId}/accept")
    public ResponseEntity<SuggestionsDTO> acceptSuggestion(@PathVariable UUID suggestionId) {
        return ResponseEntity.ok(suggestionsService.acceptSuggestion(suggestionId));
    }
}