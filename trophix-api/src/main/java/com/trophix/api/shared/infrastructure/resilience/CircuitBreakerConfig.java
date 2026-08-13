package com.trophix.api.shared.infrastructure.resilience;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class CircuitBreakerConfig {

    @Bean
    public CircuitBreaker sidecarCircuitBreaker(
            @Value("${trophix.sidecar.circuit-breaker.failure-threshold}") int failureThreshold,
            @Value("${trophix.sidecar.circuit-breaker.open-timeout}") Duration openTimeout,
            @Value("${trophix.sidecar.circuit-breaker.half-open-max-calls}") int halfOpenMaxCalls) {
        return new CircuitBreaker("psn-sidecar", failureThreshold, openTimeout, halfOpenMaxCalls);
    }
}
