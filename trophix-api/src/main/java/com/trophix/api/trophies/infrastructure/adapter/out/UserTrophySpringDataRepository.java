package com.trophix.api.trophies.infrastructure.adapter.out;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserTrophySpringDataRepository extends JpaRepository<UserTrophyEntity, UUID> {

    Optional<UserTrophyEntity> findByUserIdAndTrophyId(UUID userId, UUID trophyId);
}