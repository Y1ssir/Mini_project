package com.ems.chat.services.conversation;

import com.ems.chat.dto.conversation.ConversationRequestDTO;
import com.ems.chat.dto.conversation.ConversationResponseDTO;
import com.ems.chat.entity.ChatConversation;

import java.util.UUID;

public interface ConversationService {
    ConversationResponseDTO createConversation(ConversationRequestDTO request);
    ConversationResponseDTO GetConversation(UUID conversationId);
    ConversationResponseDTO updateStatus (UUID conversationId, ChatConversation.Statutconversation statut);
    ConversationResponseDTO updateProblemResume(UUID conversationId,String problemeResume);

}
