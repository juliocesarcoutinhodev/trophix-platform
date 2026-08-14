package com.trophix.api.auth.infrastructure.adapter.out;

import com.trophix.api.auth.model.PasswordResetToken;
import org.springframework.stereotype.Component;

@Component
public class PasswordResetTokenMapper {

    public PasswordResetToken toDomain(PasswordResetTokenJpaEntity entity) {
        return new PasswordResetToken(
                entity.getId(),
                entity.getUserId(),
                entity.getTokenHash(),
                entity.getExpiresAt(),
                entity.getCreatedAt(),
                entity.getConsumedAt());
    }

    public PasswordResetTokenJpaEntity toEntity(PasswordResetToken token) {
        PasswordResetTokenJpaEntity entity = new PasswordResetTokenJpaEntity();
        entity.setId(token.id());
        entity.setUserId(token.userId());
        entity.setTokenHash(token.tokenHash());
        entity.setExpiresAt(token.expiresAt());
        entity.setCreatedAt(token.createdAt());
        entity.setConsumedAt(token.consumedAt());
        return entity;
    }
}
