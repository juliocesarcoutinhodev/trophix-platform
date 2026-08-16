package com.trophix.api.forums.infrastructure.adapter.in.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateReplyRequest(
        @NotBlank String content) {
}
