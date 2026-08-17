package com.trophix.api.news.infrastructure.adapter.out;

import com.trophix.api.shared.infrastructure.persistence.UuidV7Id;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "news_articles")
@Getter
@Setter
public class NewsArticleJpaEntity {

    @Id
    @UuidV7Id
    private UUID id;

    @Column(nullable = false, length = 300)
    private String title;

    @Column(nullable = false, length = 1000, unique = true)
    private String link;

    @Column(length = 1000)
    private String imageUrl;

    @Column(nullable = false, length = 100)
    private String source;

    @Column(nullable = false)
    private Instant publishedAt;

    @Column(nullable = false)
    private boolean isFeatured;
}
