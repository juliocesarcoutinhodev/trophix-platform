package com.trophix.api.ai.application.ports.out;

import java.util.UUID;

/**
 * Driven port for publishing AI generation jobs to the async queue.
 */
public interface GuideAiJobPublisher {

    void publishRoadmapGeneration(UUID guideId);

    void publishTrophyTipGeneration(UUID guideId, UUID trophyId);
}
