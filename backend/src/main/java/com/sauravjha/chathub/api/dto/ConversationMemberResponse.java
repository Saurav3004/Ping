package com.sauravjha.chathub.api.dto;

import com.sauravjha.chathub.domain.MemberRole;

import java.util.UUID;

public record ConversationMemberResponse(
        UUID conversationId,
        UUID userId,
        String displayName,
        MemberRole role,
        long lastReadSequence
) {
}
