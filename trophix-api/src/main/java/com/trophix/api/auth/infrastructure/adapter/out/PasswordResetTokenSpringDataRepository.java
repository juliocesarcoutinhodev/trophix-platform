package com.trophix.api.auth.infrastructure.adapter.out;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface PasswordResetTokenSpringDataRepository extends JpaRepository<PasswordResetTokenJpaEntity, UUID> {

    Optional<PasswordResetTokenJpaEntity> findByTokenHash(String tokenHash);

    @Modifying
    @Query("delete from PasswordResetTokenJpaEntity t where t.userId = :userId")
    int deleteByUserId(@Param("userId") UUID userId);

    @Modifying
    @Query("delete from PasswordResetTokenJpaEntity t where t.expiresAt < :cutoff or t.consumedAt < :cutoff")
    int deleteBefore(@Param("cutoff") Instant cutoff);
}
