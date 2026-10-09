package com.sauravjha.chathub.api.dto;

import com.sauravjha.chathub.domain.ConversationType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;
import java.util.UUID;

public record CreateConversationRequest(
        @NotNull ConversationType type,
        @Size(max = 120) String title,
        @NotEmpty @Size(max = 250) Set<UUID> memberIds
        ) {
}
