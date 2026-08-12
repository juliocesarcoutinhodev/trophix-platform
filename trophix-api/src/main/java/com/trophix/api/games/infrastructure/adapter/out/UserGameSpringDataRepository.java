package com.trophix.api.games.infrastructure.adapter.out;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserGameSpringDataRepository extends JpaRepository<UserGameEntity, UUID> {

    Optional<UserGameEntity> findByUserIdAndGameId(UUID userId, UUID gameId);
}