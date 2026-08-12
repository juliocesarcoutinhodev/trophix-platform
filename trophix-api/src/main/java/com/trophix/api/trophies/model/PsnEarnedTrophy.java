package com.trophix.api.trophies.model;

import java.time.Instant;

/**
 * Earned status of a trophy for a user, as returned by the sidecar.
 */
public record PsnEarnedTrophy(
        Integer psnTrophyId,
        boolean earned,
        Instant earnedAt) {
}