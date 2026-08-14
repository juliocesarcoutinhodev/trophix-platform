package com.trophix.api.settings.infrastructure.adapter.out;

import com.trophix.api.settings.model.GlobalSettings;
import org.springframework.stereotype.Component;

@Component
public class GlobalSettingsMapper {

    public GlobalSettings toDomain(GlobalSettingsJpaEntity entity) {
        return new GlobalSettings(
                entity.getId(),
                entity.getSiteName(),
                entity.getContactEmail(),
                entity.getDiscordUrl(),
                entity.getTwitterUrl(),
                entity.getYoutubeUrl(),
                entity.getInstagramUrl(),
                entity.getHeroTitle(),
                entity.getHeroSubtitle(),
                entity.isGlobalAlertEnabled(),
                entity.getGlobalAlertText(),
                entity.getFooterText(),
                entity.isRequireGuideApproval(),
                entity.getForbiddenWords(),
                entity.getMetaTitle(),
                entity.getMetaDescription());
    }

    public GlobalSettingsJpaEntity toEntity(GlobalSettings settings) {
        GlobalSettingsJpaEntity entity = new GlobalSettingsJpaEntity();
        entity.setId(settings.id());
        entity.setSiteName(settings.siteName());
        entity.setContactEmail(settings.contactEmail());
        entity.setDiscordUrl(settings.discordUrl());
        entity.setTwitterUrl(settings.twitterUrl());
        entity.setYoutubeUrl(settings.youtubeUrl());
        entity.setInstagramUrl(settings.instagramUrl());
        entity.setHeroTitle(settings.heroTitle());
        entity.setHeroSubtitle(settings.heroSubtitle());
        entity.setGlobalAlertEnabled(settings.globalAlertEnabled());
        entity.setGlobalAlertText(settings.globalAlertText());
        entity.setFooterText(settings.footerText());
        entity.setRequireGuideApproval(settings.requireGuideApproval());
        entity.setForbiddenWords(settings.forbiddenWords());
        entity.setMetaTitle(settings.metaTitle());
        entity.setMetaDescription(settings.metaDescription());
        return entity;
    }
}
