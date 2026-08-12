package com.trophix.api.auth.infrastructure.adapter.in.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistrationRequest(
        @NotBlank(message = "Informe o psnId da conta PSN vinculada") String psnId,
        @NotBlank @Email(message = "Informe um e-mail válido") String email,
        @NotBlank @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres") String password) {
}