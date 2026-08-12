package com.trophix.api.games.infrastructure.adapter.out;

import com.trophix.api.shared.infrastructure.persistence.UuidV7Id;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "games")
@Getter
@Setter
public class GameEntity {

    @Id
    @UuidV7Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 64)
    private String npCommunicationId;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(length = 500)
    private String imageUrl;

    @Column(length = 100)
    private String platform;

    @Column(nullable = false)
    private Integer totalTrophies;
}