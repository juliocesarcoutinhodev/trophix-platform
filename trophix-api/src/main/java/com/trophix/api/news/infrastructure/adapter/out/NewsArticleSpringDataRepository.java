package com.trophix.api.news.infrastructure.adapter.out;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public interface NewsArticleSpringDataRepository extends JpaRepository<NewsArticleJpaEntity, UUID> {

    @Query("select n.link from NewsArticleJpaEntity n where n.link in :links")
    Set<String> findExistingLinks(@Param("links") Collection<String> links);

    Page<NewsArticleJpaEntity> findAllByOrderByPublishedAtDesc(Pageable pageable);

    @Modifying
    @Query("update NewsArticleJpaEntity n set n.isFeatured = false")
    void clearFeatured();

    @Modifying
    @Query("update NewsArticleJpaEntity n set n.isFeatured = true where n.id = :id")
    void markFeatured(@Param("id") UUID id);
}
