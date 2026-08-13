package com.trophix.api.guides.infrastructure.adapter.out;

import com.trophix.api.games.infrastructure.adapter.out.GameEntity;
import com.trophix.api.guides.model.GuideStatus;
import com.trophix.api.shared.infrastructure.persistence.UuidV7Id;
import com.trophix.api.trophies.infrastructure.adapter.out.TrophyEntity;
import com.trophix.api.users.infrastructure.adapter.out.UserJpaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "guides")
@Getter
@Setter
public class GuideEntity {

    @Id
    @UuidV7Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trophy_id")
    private TrophyEntity trophy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private GameEntity game;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private UserJpaEntity author;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(length = 500)
    private String videoUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private GuideStatus status;

    @Column(nullable = false)
    private Integer upvotesCount;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;
}