package com.trophix.api.ai.application.ports.in;

import com.trophix.api.ai.domain.GuideAiJob;

/**
 * Executes an AI generation job consumed from the queue: loads the guide (and
 * optionally the trophy), calls the LLM and persists the generated content,
 * preserving the guide moderation status.
 */
public interface GenerateGuideAiUseCase {

    void generate(GuideAiJob job);
}
