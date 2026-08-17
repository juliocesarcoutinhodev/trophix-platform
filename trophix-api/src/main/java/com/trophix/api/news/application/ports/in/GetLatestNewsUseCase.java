package com.trophix.api.news.application.ports.in;

import com.trophix.api.news.model.NewsArticle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Returns the latest news articles, newest first.
 */
public interface GetLatestNewsUseCase {

    Page<NewsArticle> getLatest(Pageable pageable);
}
