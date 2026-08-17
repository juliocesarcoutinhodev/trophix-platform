package com.trophix.api.games.infrastructure.adapter.out;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserGameSpringDataRepository extends JpaRepository<UserGameEntity, UUID> {

    Optional<UserGameEntity> findByUserIdAndGameId(UUID userId, UUID gameId);

    /**
     * Paginated query that loads the associated GameEntity in the same
     * statement (LEFT JOIN FETCH via @EntityGraph), avoiding N+1 selects
     * when reading game name/image for each row.
     */
    @EntityGraph(attributePaths = {"game"})
    Page<UserGameEntity> findByUserIdOrderByLastPlayedAtDesc(UUID userId, Pageable pageable);
}