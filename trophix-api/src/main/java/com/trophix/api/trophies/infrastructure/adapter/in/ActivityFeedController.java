package com.trophix.api.trophies.infrastructure.adapter.in;

import com.trophix.api.trophies.application.ports.in.GetActivityFeedUseCase;
import com.trophix.api.trophies.infrastructure.adapter.in.dto.ActivityFeedDTO;
import com.trophix.api.trophies.infrastructure.adapter.in.mapper.TrophyWebMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Global activity feed: who earned which trophy recently, newest first.
 */
@RestController
@RequestMapping("/api/public/trophies/feed")
@RequiredArgsConstructor
public class ActivityFeedController {

    private static final int DEFAULT_PAGE_SIZE = 20;

    private final GetActivityFeedUseCase getActivityFeedUseCase;
    private final TrophyWebMapper trophyWebMapper;

    @GetMapping
    public ResponseEntity<Page<ActivityFeedDTO>> getFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<ActivityFeedDTO> feed = getActivityFeedUseCase
                .getFeed(PageRequest.of(Math.max(page, 0), Math.min(size, 100)))
                .map(trophyWebMapper::toActivityFeedDTO);
        return ResponseEntity.ok(feed);
    }
}
