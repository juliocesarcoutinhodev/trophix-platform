package com.trophix.api.settings.application.usecases;

import com.trophix.api.settings.application.ports.in.UpdateGlobalSettingsUseCase;
import com.trophix.api.settings.application.ports.out.GlobalSettingsRepository;
import com.trophix.api.settings.model.GlobalSettings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@RequiredArgsConstructor
public class UpdateGlobalSettingsUseCaseImpl implements UpdateGlobalSettingsUseCase {

    private final GlobalSettingsRepository settingsRepository;

    @Override
    @Transactional
    public GlobalSettings update(UpdateGlobalSettingsCommand command) {
        GlobalSettings saved = settingsRepository.save(fromCommand(command));
        log.info("Configurações globais atualizadas (siteName={}, heroTitle={}, requireApproval={})",
                saved.siteName(), saved.heroTitle(), saved.requireGuideApproval());
        return saved;
    }

    private GlobalSettings fromCommand(UpdateGlobalSettingsCommand c) {
        return new GlobalSettings(
                GlobalSettings.SINGLE_ROW_ID,
                c.siteName(),
                c.contactEmail(),
                c.discordUrl(),
                c.twitterUrl(),
                c.youtubeUrl(),
                c.instagramUrl(),
                c.heroTitle(),
                c.heroSubtitle(),
                c.globalAlertEnabled(),
                c.globalAlertText(),
                c.footerText(),
                c.requireGuideApproval(),
                c.forbiddenWords(),
                c.metaTitle(),
                c.metaDescription());
    }
}
