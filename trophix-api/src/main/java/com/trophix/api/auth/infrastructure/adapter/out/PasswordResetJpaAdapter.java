package com.trophix.api.auth.infrastructure.adapter.out;

import com.trophix.api.auth.application.ports.out.PasswordResetRepository;
import com.trophix.api.auth.model.PasswordResetToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PasswordResetJpaAdapter implements PasswordResetRepository {

    private final PasswordResetTokenSpringDataRepository springDataRepository;
    private final PasswordResetTokenMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<PasswordResetToken> findByTokenHash(String tokenHash) {
        return springDataRepository.findByTokenHash(tokenHash).map(mapper::toDomain);
    }

    @Override
    @Transactional
    public PasswordResetToken save(PasswordResetToken token) {
        return mapper.toDomain(springDataRepository.save(mapper.toEntity(token)));
    }

    @Override
    @Transactional
    public int deleteBefore(Instant cutoff) {
        return springDataRepository.deleteBefore(cutoff);
    }

    @Override
    @Transactional
    public void deleteByUserId(UUID userId) {
        springDataRepository.deleteByUserId(userId);
    }
}
