package com.ems.chat.services.message;

import com.ems.chat.dto.message.ChatRequestDTO;
import com.ems.chat.dto.message.ChatResponseDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface ChatServices {
    Flux<ChatResponseDTO> ChatAnswer(UUID conversationId, ChatRequestDTO request);
    List<ChatResponseDTO> GetHistory(UUID conversation_id);
    Mono<Void> ChangeAnswer(UUID messageId, ChatRequestDTO request);
}
