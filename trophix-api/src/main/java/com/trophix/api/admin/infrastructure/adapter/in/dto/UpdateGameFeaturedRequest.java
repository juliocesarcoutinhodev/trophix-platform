package com.trophix.api.admin.infrastructure.adapter.in.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateGameFeaturedRequest(
        @NotNull(message = "O campo isFeatured é obrigatório.")
        Boolean isFeatured) {
}
