package com.trophix.api.guides.application.ports.in;

import com.trophix.api.guides.model.GuideListItem;

import java.util.UUID;

public interface GetGuideByIdUseCase {

    /**
     * Returns an APPROVED guide enriched with the target game name/image, the
     * author's name and whether the current user (nullable for anonymous)
     * voted, or throws when the guide does not exist.
     */
    GuideListItem getGuide(UUID guideId, UUID currentUserId);
}
