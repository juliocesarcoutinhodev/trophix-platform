package com.trophix.api.settings.infrastructure.adapter.in.mapper;

import com.trophix.api.settings.application.ports.in.UpdateGlobalSettingsUseCase;
import com.trophix.api.settings.infrastructure.adapter.in.dto.GlobalSettingsResponse;
import com.trophix.api.settings.model.GlobalSettings;
import org.springframework.stereotype.Component;

/**
 * Converts global settings between the web layer and the application layer.
 */
@Component
public class GlobalSettingsWebMapper {

    public GlobalSettingsResponse toResponse(GlobalSettings settings) {
        return new GlobalSettingsResponse(
                settings.siteName(),
                settings.contactEmail(),
                settings.discordUrl(),
                settings.twitterUrl(),
                settings.youtubeUrl(),
                settings.instagramUrl(),
                settings.heroTitle(),
                settings.heroSubtitle(),
                settings.globalAlertEnabled(),
                settings.globalAlertText(),
                settings.footerText(),
                settings.requireGuideApproval(),
                settings.forbiddenWords(),
                settings.metaTitle(),
                settings.metaDescription());
    }

    public UpdateGlobalSettingsUseCase.UpdateGlobalSettingsCommand toCommand(GlobalSettingsResponse response) {
        return new UpdateGlobalSettingsUseCase.UpdateGlobalSettingsCommand(
                response.siteName(),
                response.contactEmail(),
                response.discordUrl(),
                response.twitterUrl(),
                response.youtubeUrl(),
                response.instagramUrl(),
                response.heroTitle(),
                response.heroSubtitle(),
                response.globalAlertEnabled(),
                response.globalAlertText(),
                response.footerText(),
                response.requireGuideApproval(),
                response.forbiddenWords(),
                response.metaTitle(),
                response.metaDescription());
    }
}
