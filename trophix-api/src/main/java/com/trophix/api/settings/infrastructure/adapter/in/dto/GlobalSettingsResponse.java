package com.trophix.api.settings.infrastructure.adapter.in.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Global settings payload — used both as the GET response and the PUT request.
 */
public record GlobalSettingsResponse(
        @NotBlank(message = "O nome do site é obrigatório.")
        @Size(max = 100, message = "O nome do site deve ter no máximo 100 caracteres.")
        String siteName,

        @Email(message = "Informe um e-mail válido.")
        @Size(max = 320)
        String contactEmail,

        @Size(max = 500) String discordUrl,
        @Size(max = 500) String twitterUrl,
        @Size(max = 500) String youtubeUrl,
        @Size(max = 500) String instagramUrl,

        @Size(max = 255) String heroTitle,
        @Size(max = 500) String heroSubtitle,

        @NotNull(message = "Informe se o alerta global está habilitado.")
        Boolean globalAlertEnabled,
        @Size(max = 500) String globalAlertText,
        @Size(max = 500) String footerText,

        @NotNull(message = "Informe se os guias exigem aprovação.")
        Boolean requireGuideApproval,
        @Size(max = 1000) String forbiddenWords,

        @Size(max = 255) String metaTitle,
        @Size(max = 500) String metaDescription) {
}
