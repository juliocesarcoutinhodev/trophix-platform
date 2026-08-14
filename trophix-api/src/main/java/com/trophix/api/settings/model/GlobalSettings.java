package com.trophix.api.settings.model;

import java.util.UUID;

/**
 * Global platform settings (single row in the database). Pure Java.
 * {@code forbiddenWords} is a comma-separated list of words filtered in
 * user-generated content.
 */
public record GlobalSettings(
        UUID id,
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

    public static final UUID SINGLE_ROW_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    /** The built-in defaults used when no settings row exists yet. */
    public static GlobalSettings defaults() {
        return new GlobalSettings(
                SINGLE_ROW_ID,
                "Trophix",
                "",
                "",
                "",
                "",
                "",
                "Guias de troféus da PSN",
                "Ajude a comunidade a platinar seus jogos favoritos",
                false,
                "",
                "",
                true,
                "",
                "Trophix — Guias de troféus da PSN",
                "Descubra guias, dicas e roadmaps de troféus da PSN.");
    }
}
