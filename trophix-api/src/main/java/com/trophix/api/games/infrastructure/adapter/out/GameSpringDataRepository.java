package com.trophix.api.games.infrastructure.adapter.out;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface GameSpringDataRepository extends JpaRepository<GameEntity, UUID> {

    Optional<GameEntity> findByNpCommunicationId(String npCommunicationId);
}