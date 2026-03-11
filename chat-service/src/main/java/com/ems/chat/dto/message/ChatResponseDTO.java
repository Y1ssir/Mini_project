package com.ems.chat.dto.message;

public record ChatResponseDTO(
        FluxStatut type,
        String content,
        String expediteur,
        String timestamp
) {
    public enum FluxStatut{
        START,
        STREAM,
        END,
    }

}
