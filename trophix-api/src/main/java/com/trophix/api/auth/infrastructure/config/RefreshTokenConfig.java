package com.trophix.api.auth.infrastructure.config;

import com.trophix.api.auth.application.RefreshTokenPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Wires the {@link RefreshTokenPolicy} (application layer) from configuration.
 */
@Configuration
public class RefreshTokenConfig {

    @Bean
    public RefreshTokenPolicy refreshTokenPolicy(
            @Value("${trophix.refresh-token.expiration:PT720H}") Duration expiration,
            @Value("${trophix.refresh-token.idle-timeout:PT0S}") Duration idleTimeout) {
        return new RefreshTokenPolicy(expiration, idleTimeout);
    }
}
