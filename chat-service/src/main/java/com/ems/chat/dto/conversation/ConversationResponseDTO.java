package com.ems.chat.dto.conversation;

import com.ems.chat.entity.ChatConversation;

import java.time.LocalDateTime;
import java.util.UUID;

public record ConversationResponseDTO(
        ChatConversation.Statutconversation statut,
        String problemeResume,
        LocalDateTime timestamp,
        UUID id
){}
