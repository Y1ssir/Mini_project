package com.ems.chat.controller;

import com.ems.chat.dto.message.ChatRequestDTO;
import com.ems.chat.dto.message.ChatResponseDTO;
import com.ems.chat.services.message.ChatServices;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Gère le chat en temps réel via WebSocket/STOMP.
 *
 * Côté client :
 *   - Connexion   : STOMP sur /ws/chat (avec fallback SockJS)
 *   - Envoi       : SEND  /app/chat/{conversationId}   body: ChatRequestDTO
 *   - Réception   : SUBSCRIBE /topic/chat/{conversationId}  → flux de ChatResponseDTO
 *
 * Chaque chunk Spring AI est poussé individuellement jusqu'au signal END.
 */
@Controller
public class ChatWebSocketController {

    private final ChatServices chatServices;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatWebSocketController(ChatServices chatServices,
                                   SimpMessagingTemplate messagingTemplate) {
        this.chatServices = chatServices;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat/{conversationId}")
    public void handleChatMessage(
            @DestinationVariable String conversationId,
            ChatRequestDTO request
    ) {
        UUID convId = UUID.fromString(conversationId);

        // Message vide → on renvoie immédiatement un signal END
        if (request.message() == null || request.message().isBlank()) {
            messagingTemplate.convertAndSend(
                    "/topic/chat/" + conversationId,
                    new ChatResponseDTO(
                            ChatResponseDTO.FluxStatut.END,
                            "Message vide",
                            "BOT",
                            LocalDateTime.now().toString()
                    )
            );
            return;
        }

        // On souscrit au Flux Spring AI et on pousse chaque chunk via STOMP
        chatServices.ChatAnswer(convId, request)
                .subscribe(
                        chunk -> messagingTemplate.convertAndSend("/topic/chat/" + conversationId, chunk),
                        error -> messagingTemplate.convertAndSend(
                                "/topic/chat/" + conversationId,
                                new ChatResponseDTO(
                                        ChatResponseDTO.FluxStatut.END,
                                        "Erreur : " + error.getMessage(),
                                        "BOT",
                                        LocalDateTime.now().toString()
                                )
                        )
                );
    }
}
