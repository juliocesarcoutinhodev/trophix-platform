package com.trophix.api.admin.infrastructure.adapter.in.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateGuideRequest(
        @NotBlank(message = "O título do guia é obrigatório.")
        @Size(max = 255, message = "O título do guia deve ter no máximo 255 caracteres.")
        String title,

        @Size(max = 1000, message = "A descrição do guia deve ter no máximo 1000 caracteres.")
        String description,

        @NotBlank(message = "O conteúdo do guia não pode estar vazio")
        String content,

        String videoUrl) {
}
