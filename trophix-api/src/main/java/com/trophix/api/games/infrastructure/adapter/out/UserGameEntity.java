package com.trophix.api.games.infrastructure.adapter.out;

import com.trophix.api.shared.infrastructure.persistence.UuidV7Id;
import com.trophix.api.users.infrastructure.adapter.out.UserJpaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_games",
        uniqueConstraints = @UniqueConstraint(name = "uq_user_games_user_game",
                columnNames = {"user_id", "game_id"}))
@Getter
@Setter
public class UserGameEntity {

    @Id
    @UuidV7Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserJpaEntity user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "game_id", nullable = false)
    private GameEntity game;

    @Column(nullable = false)
    private Integer progressPercentage;

    @Column(nullable = false)
    private Integer earnedTrophies;

    @Column(nullable = false)
    private Instant lastPlayedAt;
}