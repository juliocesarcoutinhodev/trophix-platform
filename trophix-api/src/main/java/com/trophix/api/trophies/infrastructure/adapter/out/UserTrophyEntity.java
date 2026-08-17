package com.trophix.api.trophies.infrastructure.adapter.out;

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

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_trophies",
        uniqueConstraints = @UniqueConstraint(name = "uq_user_trophies_user_trophy",
                columnNames = {"user_id", "trophy_id"}))
@Getter
@Setter
public class UserTrophyEntity {

    @Id
    @UuidV7Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trophy_id", nullable = false)
    private TrophyEntity trophy;

    @Column(nullable = false)
    private Instant earnedAt;
}