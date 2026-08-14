package com.trophix.api.auth.infrastructure.adapter.out;

import com.trophix.api.auth.model.RefreshToken;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenMapper {

    public RefreshToken toDomain(RefreshTokenJpaEntity entity) {
        return new RefreshToken(
                entity.getId(),
                entity.getFamilyId(),
                entity.getUserId(),
                entity.getTokenHash(),
                entity.getExpiresAt(),
                entity.getCreatedAt(),
                entity.getRevokedAt(),
                entity.getLastUsedAt(),
                entity.getUserAgent(),
                entity.getIpAddress());
    }

    public RefreshTokenJpaEntity toEntity(RefreshToken token) {
        RefreshTokenJpaEntity entity = new RefreshTokenJpaEntity();
        entity.setId(token.id());
        entity.setFamilyId(token.familyId());
        entity.setUserId(token.userId());
        entity.setTokenHash(token.tokenHash());
        entity.setExpiresAt(token.expiresAt());
        entity.setCreatedAt(token.createdAt());
        entity.setRevokedAt(token.revokedAt());
        entity.setLastUsedAt(token.lastUsedAt());
        entity.setUserAgent(token.userAgent());
        entity.setIpAddress(token.ipAddress());
        return entity;
    }
}
