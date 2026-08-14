package com.trophix.api.auth.infrastructure.adapter.out;

import com.trophix.api.auth.application.ports.out.RefreshTokenRepository;
import com.trophix.api.auth.model.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class RefreshTokenJpaAdapter implements RefreshTokenRepository {

    private final RefreshTokenSpringDataRepository springDataRepository;
    private final RefreshTokenMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<RefreshToken> findByHash(String tokenHash) {
        return springDataRepository.findByTokenHash(tokenHash).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RefreshToken> findByHashForUpdate(String tokenHash) {
        return springDataRepository.findByTokenHashForUpdate(tokenHash).map(mapper::toDomain);
    }

    @Override
    @Transactional
    public RefreshToken save(RefreshToken token) {
        return mapper.toDomain(springDataRepository.save(mapper.toEntity(token)));
    }

    @Override
    @Transactional
    public int revokeFamily(UUID familyId) {
        return springDataRepository.revokeFamily(familyId, Instant.now());
    }

    @Override
    @Transactional
    public int revokeAllForUser(UUID userId) {
        return springDataRepository.revokeAllForUser(userId, Instant.now());
    }

    @Override
    @Transactional
    public int deleteExpiredBefore(Instant cutoff) {
        return springDataRepository.deleteExpiredBefore(cutoff);
    }
}
