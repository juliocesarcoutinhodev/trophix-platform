package com.trophix.api.ai.application.ports.out;

import com.trophix.api.ai.domain.GuideAiPrompt;

/**
 * Driven port for LLM-backed guide generation. The application layer only
 * knows this abstraction — the Spring AI / Gemini integration stays confined
 * to the infrastructure adapter that implements it.
 */
public interface AiGuideGeneratorPort {

    /** Generates the full roadmap (platinum) guide body for a game. */
    String generateRoadmapContent(GuideAiPrompt prompt);

    /** Generates a focused tip for a single trophy. */
    String generateTrophyTipContent(GuideAiPrompt prompt);
}
