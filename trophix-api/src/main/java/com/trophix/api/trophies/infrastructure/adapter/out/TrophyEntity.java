package com.trophix.api.trophies.infrastructure.adapter.out;

import com.trophix.api.shared.infrastructure.persistence.UuidV7Id;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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

    @Column(name = "game_id", nullable = false)
    private UUID gameId;

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