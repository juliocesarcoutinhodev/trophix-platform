package com.trophix.api.guides.application.ports.in;

import java.util.UUID;

public interface SubmitGuideUseCase {

    /**
     * Submits a guide (trophy or game roadmap) with initial PENDING status.
     * At least one target (trophyId or gameId) must be informed.
     *
     * @return success message in Brazilian Portuguese
     */
    String submit(UUID authorId, SubmitGuideCommand command);

    record SubmitGuideCommand(UUID trophyId, UUID gameId, String content, String videoUrl) {
    }
}