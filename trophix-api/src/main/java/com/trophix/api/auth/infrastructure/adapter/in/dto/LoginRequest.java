package com.trophix.api.auth.infrastructure.adapter.in.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank @Email(message = "Informe um e-mail válido") String email,
        @NotBlank(message = "Informe a senha") String password) {
}