package com.trophix.api.admin.application.ports.in;

import com.trophix.api.guides.model.GuideListItem;
import com.trophix.api.guides.model.GuideStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Paginated list of every guide in the platform, optionally filtered by
 * moderation status and/or a free-text search on the title or game name,
 * enriched with the author and target game names.
 */
public interface ListAllGuidesUseCase {

    Page<GuideListItem> listAllGuides(GuideStatus status, String search, Boolean isTrophyGuide, Pageable pageable);
}
