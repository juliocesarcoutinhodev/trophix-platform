package com.trophix.api.auth.application;

import java.time.Duration;

/**
 * Refresh session policy. Plain application-layer value object wired from
 * configuration by the infrastructure layer (see {@code RefreshTokenConfig}).
 *
 * @param expiration   absolute TTL of each refresh token
 * @param idleTimeout  sliding inactivity timeout ({@link Duration#ZERO} disables)
 */
public record RefreshTokenPolicy(Duration expiration, Duration idleTimeout) {

    public RefreshTokenPolicy {
        if (expiration == null || expiration.isNegative() || expiration.isZero()) {
            throw new IllegalArgumentException("Refresh token expiration must be positive");
        }
        if (idleTimeout == null) {
            idleTimeout = Duration.ZERO;
        }
    }
}
