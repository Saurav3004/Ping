package com.sauravjha.chathub.api.dto;

import java.util.List;

public record MessagePageResponse(
        List<MessageResponse> messages
) {
}
