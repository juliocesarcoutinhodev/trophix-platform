package com.trophix.api.guides.infrastructure.adapter.out;

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
@Table(name = "guide_votes",
        uniqueConstraints = @UniqueConstraint(name = "uq_guide_votes_guide_user",
                columnNames = {"guide_id", "user_id"}))
@Getter
@Setter
public class GuideVoteEntity {

    @Id
    @UuidV7Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "guide_id", nullable = false)
    private GuideEntity guide;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private Instant votedAt;
}