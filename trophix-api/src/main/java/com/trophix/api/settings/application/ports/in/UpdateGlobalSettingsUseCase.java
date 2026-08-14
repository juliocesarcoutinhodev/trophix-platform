package com.trophix.api.settings.application.ports.in;

import com.trophix.api.settings.model.GlobalSettings;

/**
 * Replaces and persists the global settings (admin).
 */
public interface UpdateGlobalSettingsUseCase {

    GlobalSettings update(UpdateGlobalSettingsCommand command);

    record UpdateGlobalSettingsCommand(
            String siteName,
            String contactEmail,
            String discordUrl,
            String twitterUrl,
            String youtubeUrl,
            String instagramUrl,
            String heroTitle,
            String heroSubtitle,
            boolean globalAlertEnabled,
            String globalAlertText,
            String footerText,
            boolean requireGuideApproval,
            String forbiddenWords,
            String metaTitle,
            String metaDescription) {
    }
}
