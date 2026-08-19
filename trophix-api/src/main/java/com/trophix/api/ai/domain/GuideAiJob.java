package com.trophix.api.ai.domain;

import java.util.UUID;

/**
 * Message contract for the asynchronous AI guide generation queue.
 * Pure Java, serialized as JSON by the AMQP infrastructure.
 */
public record GuideAiJob(Type type, UUID guideId, UUID trophyId) {

    public enum Type {
        ROADMAP_GENERATION,
        TROPHY_TIP_GENERATION
    }

    public static GuideAiJob roadmap(UUID guideId) {
        return new GuideAiJob(Type.ROADMAP_GENERATION, guideId, null);
    }

    public static GuideAiJob trophyTip(UUID guideId, UUID trophyId) {
        return new GuideAiJob(Type.TROPHY_TIP_GENERATION, guideId, trophyId);
    }
}
