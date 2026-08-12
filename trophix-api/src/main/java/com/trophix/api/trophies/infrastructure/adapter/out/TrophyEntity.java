package com.trophix.api.trophies.infrastructure.adapter.out;

import com.trophix.api.games.infrastructure.adapter.out.GameEntity;
import com.trophix.api.shared.infrastructure.persistence.UuidV7Id;
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

import java.util.UUID;

@Entity
@Table(name = "trophies",
        uniqueConstraints = @UniqueConstraint(name = "uq_trophies_game_psn",
                columnNames = {"game_id", "psn_trophy_id"}))
@Getter
@Setter
public class TrophyEntity {

    @Id
    @UuidV7Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "game_id", nullable = false)
    private GameEntity game;

    @Column(nullable = false)
    private Integer psnTrophyId;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(nullable = false, length = 20)
    private String type;

    @Column(length = 500)
    private String iconUrl;
}