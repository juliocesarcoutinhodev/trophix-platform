package com.trophix.api.ai.domain;

/**
 * Pure input for the AI generator port. Carries only the facts needed to
 * build the prompt: the exact game name, the platform and, when generating a
 * trophy tip, the trophy name and description.
 */
public record GuideAiPrompt(
        String gameName,
        String platform,
        String trophyName,
        String trophyDescription) {
}
