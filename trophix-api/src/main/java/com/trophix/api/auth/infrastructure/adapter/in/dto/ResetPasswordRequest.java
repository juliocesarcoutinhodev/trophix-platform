package com.trophix.api.auth.infrastructure.adapter.in.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
        @NotBlank(message = "Token de redefinição ausente") String token,
        @NotBlank @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres") String newPassword) {
}
