package com.sauravjha.chathub.api.dto;

import com.sauravjha.chathub.domain.MemberRole;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AddMemberRequest(
        @NotNull UUID userId,
        @NotNull MemberRole role
) {
}
