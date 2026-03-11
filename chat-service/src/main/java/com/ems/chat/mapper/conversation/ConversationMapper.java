package com.ems.chat.mapper.conversation;

import com.ems.chat.dto.conversation.ConversationRequestDTO;
import com.ems.chat.dto.conversation.ConversationResponseDTO;
import com.ems.chat.entity.ChatConversation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ConversationMapper {
    @Mapping(source = "dateCreation",target = "timestamp")
    ConversationResponseDTO toDTO(ChatConversation conversation);
    @Mapping(source = "userId",target = "utilisateurId")
    ChatConversation toEntity(ConversationRequestDTO request);
}
