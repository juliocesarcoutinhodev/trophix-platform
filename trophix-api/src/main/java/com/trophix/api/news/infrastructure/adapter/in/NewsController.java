package com.trophix.api.news.infrastructure.adapter.in;

import com.trophix.api.news.application.ports.in.GetLatestNewsUseCase;
import com.trophix.api.news.infrastructure.adapter.in.dto.NewsArticleResponse;
import com.trophix.api.news.infrastructure.adapter.in.mapper.NewsWebMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Public news feed, newest first. Accessible without authentication.
 */
@RestController
@RequestMapping("/api/public/news")
@RequiredArgsConstructor
public class NewsController {

    private static final int DEFAULT_PAGE_SIZE = 20;

    private final GetLatestNewsUseCase getLatestNewsUseCase;
    private final NewsWebMapper newsWebMapper;

    @GetMapping
    public ResponseEntity<Page<NewsArticleResponse>> getLatest(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<NewsArticleResponse> result = getLatestNewsUseCase
                .getLatest(PageRequest.of(Math.max(page, 0), Math.min(size, 100)))
                .map(newsWebMapper::toResponse);
        return ResponseEntity.ok(result);
    }
}
