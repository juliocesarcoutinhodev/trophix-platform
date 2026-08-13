package com.trophix.api.shared.infrastructure.ratelimit;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "trophix.rate-limit")
public record RateLimitProperties(
        boolean enabled,
        boolean trustForwardedHeader,
        Map<String, Limit> limits) {

    public RateLimitProperties {
        limits = limits == null ? Map.of() : limits;
    }

    public record Limit(int capacity, int refillPerMinute) {
    }
}
