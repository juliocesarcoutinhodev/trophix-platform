package com.trophix.api.admin.application.ports.in;

import com.trophix.api.guides.model.GuideListItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Paginated list of every guide in the platform, regardless of moderation
 * status (PENDING, APPROVED, REJECTED), enriched with the author and target
 * game names.
 */
public interface ListAllGuidesUseCase {

    Page<GuideListItem> listAllGuides(Pageable pageable);
}
