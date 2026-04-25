package com.ems.chat.mapper.suggestion;

import com.ems.chat.dto.suggestion.SuggestionsDTO;
import com.ems.chat.entity.Suggestions;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SuggestionsMapper {
    @Mapping(source = "conversation.id", target = "conversationId")
    SuggestionsDTO toDTO(Suggestions suggestion);

    @Mapping(target = "conversation", ignore = true)
    Suggestions toEntity(SuggestionsDTO dto);
}