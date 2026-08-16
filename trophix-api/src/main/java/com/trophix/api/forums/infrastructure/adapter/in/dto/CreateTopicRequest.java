package com.trophix.api.forums.infrastructure.adapter.in.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateTopicRequest(
        @NotNull UUID categoryId,
        @NotBlank String title,
        @NotBlank String content) {
}
