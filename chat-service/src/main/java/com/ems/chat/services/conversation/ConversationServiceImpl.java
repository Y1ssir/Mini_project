package com.ems.chat.services.conversation;

import com.ems.chat.dto.conversation.ConversationRequestDTO;
import com.ems.chat.dto.conversation.ConversationResponseDTO;
import com.ems.chat.entity.ChatConversation;
import com.ems.chat.mapper.conversation.ConversationMapper;
import com.ems.chat.repository.ChatConversationRepo;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Transactional
public class ConversationServiceImpl implements ConversationService {
    private final ConversationMapper conversationMapper;
    private final ChatConversationRepo conversationRepo;

    public ConversationServiceImpl(
            ConversationMapper conversationMapper,
            ChatConversationRepo conversationRepo){
      this.conversationMapper=conversationMapper;
      this.conversationRepo=conversationRepo;
    }
    @Override
    public ConversationResponseDTO createConversation(ConversationRequestDTO request){
        ChatConversation newConversation= conversationMapper.toEntity(request);
        conversationRepo.save(newConversation);
        return conversationMapper.toDTO(newConversation);
    }
    private ChatConversation FindConversation(UUID conversationId){
        return conversationRepo.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation introuvable"));
    }
    @Override
    public ConversationResponseDTO GetConversation(UUID conversationId){
        ChatConversation conversation=FindConversation(conversationId);
        return conversationMapper.toDTO(conversation);
    };
    @Override
    public ConversationResponseDTO updateStatus (UUID conversationId, ChatConversation.Statutconversation statut){
        ChatConversation conversation=FindConversation(conversationId);
        conversation.setStatut(statut);
        conversationRepo.save(conversation);
        return conversationMapper.toDTO(conversation);

    };
    @Override
    public ConversationResponseDTO updateProblemResume(UUID conversationId,String problemeResume){
        ChatConversation conversation=FindConversation(conversationId);
        conversation.setProblemeResume(problemeResume);
        conversationRepo.save(conversation);
        return conversationMapper.toDTO(conversation);
    };
}
