package com.trophix.api.auth.infrastructure.config;

import com.trophix.api.auth.application.PasswordResetPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Wires the {@link PasswordResetPolicy} (application layer) from configuration.
 */
@Configuration
public class PasswordResetConfig {

    @Bean
    public PasswordResetPolicy passwordResetPolicy(
            @Value("${trophix.password-reset.token-ttl:PT1H}") Duration tokenTtl,
            @Value("${trophix.password-reset.frontend-url:http://localhost:4200}") String frontendUrl) {
        return new PasswordResetPolicy(tokenTtl, frontendUrl);
    }
}
