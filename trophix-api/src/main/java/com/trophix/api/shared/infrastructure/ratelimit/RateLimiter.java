package com.trophix.api.shared.infrastructure.ratelimit;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Lightweight thread-safe token bucket rate limiter keyed by an arbitrary
 * string (e.g. client IP + endpoint group). Buckets are lazily created and
 * evicted when the map grows too large, bounding memory usage.
 */
@Slf4j
public class RateLimiter {

    private static final int MAX_BUCKETS = 10_000;
    private static final long IDLE_CLEANUP_MILLIS = 10 * 60 * 1000;

    private final String name;
    private final int capacity;
    private final double refillPerSecond;
    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    public RateLimiter(String name, int capacity, int refillPerMinute) {
        this.name = name;
        this.capacity = Math.max(1, capacity);
        this.refillPerSecond = Math.max(0.1, refillPerMinute / 60.0);
    }

    /** Consumes one token for the key, returning {@code false} when the bucket is empty. */
    public boolean tryAcquire(String key) {
        long now = System.currentTimeMillis();
        Bucket bucket = buckets.compute(key, (k, b) -> b != null ? b : new Bucket(capacity, now));
        maybeCleanup(now);
        return bucket.tryConsume(now);
    }

    private void maybeCleanup(long now) {
        if (buckets.size() < MAX_BUCKETS) {
            return;
        }
        long cutoff = now - IDLE_CLEANUP_MILLIS;
        buckets.entrySet().removeIf(entry -> entry.getValue().isIdle(cutoff));
        log.info("[{}] Limpeza de buckets: restantes={}", name, buckets.size());
    }

    private final class Bucket {
        private final int capacity;
        private double tokens;
        private long lastRefill;

        Bucket(int capacity, long now) {
            this.capacity = capacity;
            this.tokens = capacity;
            this.lastRefill = now;
        }

        synchronized boolean tryConsume(long now) {
            refill(now);
            if (tokens >= 1) {
                tokens -= 1;
                return true;
            }
            return false;
        }

        private void refill(long now) {
            double elapsedSeconds = (now - lastRefill) / 1000.0;
            tokens = Math.min(capacity, tokens + elapsedSeconds * refillPerSecond);
            lastRefill = now;
        }

        synchronized boolean isIdle(long cutoff) {
            return lastRefill < cutoff && tokens >= capacity;
        }
    }
}
