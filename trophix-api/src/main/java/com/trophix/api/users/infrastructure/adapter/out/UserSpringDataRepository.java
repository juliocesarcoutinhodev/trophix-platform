package com.trophix.api.users.infrastructure.adapter.out;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserSpringDataRepository extends JpaRepository<UserJpaEntity, UUID> {

    Optional<UserJpaEntity> findByUsername(String username);

    Optional<UserJpaEntity> findByEmail(String email);

    @Query("select count(u) from UserJpaEntity u where u.createdAt >= :since")
    long countCreatedSince(@Param("since") Instant since);

    @Modifying
    @Query("update UserJpaEntity u set u.lastSyncedAt = :when where u.id = :id")
    void updateLastSyncedAt(@Param("id") UUID id, @Param("when") Instant when);

    @Query("select u.id from UserJpaEntity u where u.lastSyncedAt is not null and u.lastSyncedAt >= :since")
    List<UUID> findActiveUserIds(@Param("since") Instant since);
}