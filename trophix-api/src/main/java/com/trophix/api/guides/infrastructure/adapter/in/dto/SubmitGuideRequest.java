package com.trophix.api.guides.infrastructure.adapter.in.dto;

import jakarta.validation.constraints.NotBlank;

public record SubmitGuideRequest(
        @NotBlank(message = "O conteúdo do guia não pode estar vazio") String content,
        String videoUrl) {
}