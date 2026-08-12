package com.trophix.api.users.infrastructure.adapter.in.dto;

import jakarta.validation.constraints.NotBlank;

public record AccountLinkRequest(
        @NotBlank(message = "Informe o psnId do jogador") String psnId) {
}