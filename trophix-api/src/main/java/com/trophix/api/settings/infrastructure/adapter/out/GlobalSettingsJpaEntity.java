package com.trophix.api.settings.infrastructure.adapter.out;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "global_settings")
@Getter
@Setter
public class GlobalSettingsJpaEntity {

    // Id fixo (single-row): atribuído pelo domínio, sem gerador de UUID.
    @Id
    private UUID id;

    @Column(name = "site_name", nullable = false, length = 100)
    private String siteName;

    @Column(name = "contact_email", nullable = false, length = 320)
    private String contactEmail;

    @Column(name = "discord_url", nullable = false, length = 500)
    private String discordUrl;

    @Column(name = "twitter_url", nullable = false, length = 500)
    private String twitterUrl;

    @Column(name = "youtube_url", nullable = false, length = 500)
    private String youtubeUrl;

    @Column(name = "instagram_url", nullable = false, length = 500)
    private String instagramUrl;

    @Column(name = "hero_title", nullable = false, length = 255)
    private String heroTitle;

    @Column(name = "hero_subtitle", nullable = false, length = 500)
    private String heroSubtitle;

    @Column(name = "global_alert_enabled", nullable = false)
    private boolean globalAlertEnabled;

    @Column(name = "global_alert_text", nullable = false, length = 500)
    private String globalAlertText;

    @Column(name = "footer_text", nullable = false, length = 500)
    private String footerText;

    @Column(name = "require_guide_approval", nullable = false)
    private boolean requireGuideApproval;

    @Column(name = "forbidden_words", nullable = false, length = 1000)
    private String forbiddenWords;

    @Column(name = "meta_title", nullable = false, length = 255)
    private String metaTitle;

    @Column(name = "meta_description", nullable = false, length = 500)
    private String metaDescription;
}
