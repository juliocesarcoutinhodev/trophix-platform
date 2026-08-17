package com.trophix.api.news.infrastructure.adapter.out;

import com.trophix.api.news.model.NewsArticle;
import org.springframework.stereotype.Component;

@Component
public class NewsArticleMapper {

    public NewsArticle toDomain(NewsArticleJpaEntity entity) {
        return new NewsArticle(
                entity.getId(),
                entity.getTitle(),
                entity.getLink(),
                entity.getImageUrl(),
                entity.getSource(),
                entity.getPublishedAt(),
                entity.isFeatured());
    }

    public NewsArticleJpaEntity toEntity(NewsArticle article) {
        NewsArticleJpaEntity entity = new NewsArticleJpaEntity();
        entity.setId(article.id());
        entity.setTitle(article.title());
        entity.setLink(article.link());
        entity.setImageUrl(article.imageUrl());
        entity.setSource(article.source());
        entity.setPublishedAt(article.publishedAt());
        entity.setFeatured(article.isFeatured());
        return entity;
    }
}
