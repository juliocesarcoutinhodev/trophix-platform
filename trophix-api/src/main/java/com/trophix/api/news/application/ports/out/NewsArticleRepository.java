package com.trophix.api.news.application.ports.out;

import com.trophix.api.news.model.NewsArticle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Driven port: persistence for {@link NewsArticle}.
 */
public interface NewsArticleRepository {

    /**
     * Persists only the articles whose {@code link} is not yet stored (dedup by
     * URL), returning exactly the ones that were saved. Used by the worker to
     * know which articles are brand new.
     */
    List<NewsArticle> saveIfNotExists(List<NewsArticle> articles);

    Page<NewsArticle> findLatest(Pageable pageable);

    /** Un-flags the current featured article and features the newest one. */
    void featureNewest();
}
