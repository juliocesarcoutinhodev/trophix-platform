package com.trophix.api.users.model;

/**
 * PSN profile summary (level, progress and trophy totals) fetched
 * from the sidecar via the profile summary endpoint.
 */
public record PsnProfileSummary(
        String accountId,
        Integer level,
        Integer progress,
        Integer bronze,
        Integer silver,
        Integer gold,
        Integer platinum) {
}