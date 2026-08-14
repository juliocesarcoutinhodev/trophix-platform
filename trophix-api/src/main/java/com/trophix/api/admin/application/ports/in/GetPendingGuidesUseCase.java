package com.trophix.api.admin.application.ports.in;

import com.trophix.api.guides.model.GuideListItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Paginated list of guides awaiting moderation (status PENDING), enriched with
 * the author and target game names.
 */
public interface GetPendingGuidesUseCase {

    Page<GuideListItem> getPendingGuides(Pageable pageable);
}
