package com.sauravjha.chathub.api.dto;

import com.sauravjha.chathub.domain.type.ReceiptType;
import jakarta.validation.constraints.NotNull;

public record ReceiptRequest(
        @NotNull ReceiptType type
        ) {
}
