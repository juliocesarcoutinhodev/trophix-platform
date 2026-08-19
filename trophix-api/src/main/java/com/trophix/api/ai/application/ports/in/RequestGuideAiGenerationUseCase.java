package com.trophix.api.ai.application.ports.in;

import java.util.UUID;

/**
 * Dispatches AI generation requests to the async queue. Endpoints call this
 * port and return 202 immediately; the actual LLM work happens on the worker.
 */
public interface RequestGuideAiGenerationUseCase {

    void requestRoadmapGeneration(UUID guideId);

    void requestTrophyTipGeneration(UUID guideId, UUID trophyId);
}
