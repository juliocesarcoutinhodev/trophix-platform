package com.trophix.api.guides.application.ports.in;

import com.trophix.api.guides.model.Guide;

import java.util.List;
import java.util.UUID;

public interface GetTrophyGuidesUseCase {

    /**
     * Returns the APPROVED guides of a trophy, ordered by upvotes (descending).
     */
    List<Guide> getApprovedGuides(UUID trophyId);
}