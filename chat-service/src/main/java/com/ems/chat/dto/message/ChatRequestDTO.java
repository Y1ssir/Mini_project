package com.ems.chat.dto.message;

import java.util.UUID;

public record ChatRequestDTO(
        UUID conversationId,
        UUID messageId,
        String message
) {}