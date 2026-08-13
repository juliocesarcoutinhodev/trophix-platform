package com.trophix.api.shared.infrastructure.resilience;

import com.trophix.api.shared.exception.CircuitOpenException;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Lightweight thread-safe circuit breaker (CLOSED / OPEN / HALF_OPEN).
 * <p>
 * Guards calls to a downstream service (the PSN sidecar): when the service
 * starts failing, the breaker trips to OPEN and calls fail fast with a
 * {@link CircuitOpenException} instead of waiting on timeouts; after
 * {@code openTimeout} it moves to HALF_OPEN and allows a few probe calls to
 * decide whether to close again.
 * <p>
 * The lock is only held for state checks/transitions, never during the actual
 * call, so it does not serialize downstream requests.
 */
@Slf4j
public class CircuitBreaker {

    public enum State { CLOSED, OPEN, HALF_OPEN }

    private final String name;
    private final int failureThreshold;
    private final Duration openTimeout;
    private final int halfOpenMaxCalls;

    private State state = State.CLOSED;
    private int consecutiveFailures;
    private Instant openedAt;
    private int halfOpenCalls;
    private int halfOpenSuccesses;

    public CircuitBreaker(String name, int failureThreshold, Duration openTimeout, int halfOpenMaxCalls) {
        this.name = name;
        this.failureThreshold = Math.max(1, failureThreshold);
        this.openTimeout = openTimeout;
        this.halfOpenMaxCalls = Math.max(1, halfOpenMaxCalls);
    }

    /**
     * Executes the call guarded by the breaker. Failures are evaluated by
     * {@code countsAsFailure} — exceptions that are not downstream outages
     * (e.g. a 404 "not found") should return {@code false} so they do not trip
     * the breaker.
     */
    public <T> T execute(Supplier<T> call, Predicate<Throwable> countsAsFailure) {
        allowCall();
        try {
            T result = call.get();
            onSuccess();
            return result;
        } catch (Throwable ex) {
            onFailure(countsAsFailure.test(ex));
            throw ex;
        }
    }

    public synchronized State state() {
        transitionToHalfOpenIfTimeoutExpired();
        return state;
    }

    private synchronized void allowCall() {
        transitionToHalfOpenIfTimeoutExpired();
        if (state == State.OPEN) {
            throw new CircuitOpenException(
                    "A PSN está temporariamente indisponível. Tente novamente em instantes.");
        }
        if (state == State.HALF_OPEN) {
            if (halfOpenCalls >= halfOpenMaxCalls) {
                throw new CircuitOpenException(
                        "A PSN está temporariamente indisponível. Tente novamente em instantes.");
            }
            halfOpenCalls++;
        }
    }

    private synchronized void onSuccess() {
        if (state == State.HALF_OPEN) {
            halfOpenSuccesses++;
            if (halfOpenSuccesses >= halfOpenMaxCalls) {
                log.info("[{}] Circuito fechado após probes bem-sucedidos.", name);
                reset();
            }
            return;
        }
        consecutiveFailures = 0;
    }

    private synchronized void onFailure(boolean countsAsFailure) {
        if (!countsAsFailure) {
            return;
        }
        if (state == State.HALF_OPEN) {
            log.warn("[{}] Probe falhou no meio-aberto; reabrindo o circuito.", name);
            open();
            return;
        }
        consecutiveFailures++;
        if (consecutiveFailures >= failureThreshold) {
            open();
        }
    }

    private void transitionToHalfOpenIfTimeoutExpired() {
        if (state == State.OPEN && openedAt != null
                && Instant.now().isAfter(openedAt.plus(openTimeout))) {
            state = State.HALF_OPEN;
            halfOpenCalls = 0;
            halfOpenSuccesses = 0;
            log.info("[{}] Circuito em meio-aberto após timeout; permitindo probes.", name);
        }
    }

    private void open() {
        state = State.OPEN;
        openedAt = Instant.now();
        log.warn("[{}] Circuito aberto após {} falhas; rejeitando chamadas por {}.",
                name, consecutiveFailures, openTimeout);
    }

    private void reset() {
        state = State.CLOSED;
        consecutiveFailures = 0;
        halfOpenCalls = 0;
        halfOpenSuccesses = 0;
        openedAt = null;
    }
}
