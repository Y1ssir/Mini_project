package com.ems.chat.mapper.message;
import com.ems.chat.dto.message.ChatRequestDTO;
import com.ems.chat.dto.message.ChatResponseDTO;
import com.ems.chat.entity.ChatMessages;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ChatMapper {
    @Mapping(source = "message", target = "content")
    @Mapping(target = "type", constant = "END")
    @Mapping(expression = "java(message.getExpediteur() != null ? message.getExpediteur().name() : null)", target = "expediteur")
    @Mapping(expression = "java(message.getTimestamp() != null ? message.getTimestamp().toString() : null)", target = "timestamp")
    ChatResponseDTO toDTO(ChatMessages message);
    List<ChatResponseDTO> toDTOList(List<ChatMessages> messages);
    @Mapping(target = "conversation", ignore = true)
    ChatMessages toEntity(ChatRequestDTO request);
}
