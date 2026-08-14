package com.trophix.api.auth.application;

import java.time.Duration;

/**
 * Password reset policy. Plain application-layer value object wired from
 * configuration by the infrastructure layer.
 *
 * @param tokenTtl     lifetime of a single-use reset token
 * @param frontendUrl  base URL of the SPA (used to build the reset link)
 */
public record PasswordResetPolicy(Duration tokenTtl, String frontendUrl) {

    public PasswordResetPolicy {
        if (tokenTtl == null || tokenTtl.isNegative() || tokenTtl.isZero()) {
            throw new IllegalArgumentException("Password reset token TTL must be positive");
        }
        if (frontendUrl == null || frontendUrl.isBlank()) {
            throw new IllegalArgumentException("Password reset frontend URL must not be blank");
        }
    }

    /** The full link delivered by e-mail. */
    public String resetUrl(String token) {
        return frontendUrl + "/reset-password?token=" + token;
    }
}
