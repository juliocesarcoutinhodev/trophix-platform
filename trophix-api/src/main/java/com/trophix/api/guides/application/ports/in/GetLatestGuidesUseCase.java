package com.trophix.api.guides.application.ports.in;

import com.trophix.api.guides.model.GuideListItem;

import java.util.List;
import java.util.UUID;

public interface GetLatestGuidesUseCase {

    /**
     * Returns the latest APPROVED game roadmaps (guides without a trophy
     * target), ordered by creation date (newest first), up to the limit,
     * enriched with the game name/image, the author's name and whether the
     * current user (nullable for anonymous) voted.
     */
    List<GuideListItem> getLatestRoadmaps(int limit, UUID currentUserId);
}
