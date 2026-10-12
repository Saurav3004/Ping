package com.sauravjha.chathub.api.dto;

import com.sauravjha.chathub.domain.type.MessageType;

import java.time.Instant;
import java.util.UUID;

public record MessageResponse(
        UUID id,
        UUID conversationId,
        UUID senderId,
        String clientMessageId,
        long sequenceNumber,
        MessageType messageType,
        String content,
        Instant createdAt
) {
}
