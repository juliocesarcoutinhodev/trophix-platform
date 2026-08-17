package com.trophix.api.trophies.application.ports.in;

import com.trophix.api.trophies.model.ActivityFeedItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Global activity feed: recently earned trophies, newest first.
 */
public interface GetActivityFeedUseCase {

    Page<ActivityFeedItem> getFeed(Pageable pageable);
}
