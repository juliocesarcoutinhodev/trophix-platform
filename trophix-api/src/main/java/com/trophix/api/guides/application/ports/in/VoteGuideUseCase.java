package com.trophix.api.guides.application.ports.in;

import java.util.UUID;

public interface VoteGuideUseCase {

    /**
     * Toggles the user's vote on a guide: registers when absent, removes
     * when present, always keeping the counter consistent.
     */
    VoteResult vote(UUID guideId, UUID userId);

    record VoteResult(boolean voted, int upvotesCount, String message) {
    }
}