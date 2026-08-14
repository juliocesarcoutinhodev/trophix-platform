package com.trophix.api.auth.infrastructure.adapter.in.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordRequest(
        @NotBlank(message = "Informe o seu e-mail") @Email(message = "Informe um e-mail válido") String email) {
}
