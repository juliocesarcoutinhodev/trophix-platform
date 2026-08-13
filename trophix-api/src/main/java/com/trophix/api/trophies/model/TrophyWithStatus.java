package com.trophix.api.trophies.model;

import java.time.Instant;

/**
 * Trophy catalog entry enriched with the user's earning status. Pure Java.
 */
public record TrophyWithStatus(
        Trophy trophy,
        boolean earned,
        Instant earnedAt) {

    public static TrophyWithStatus earned(Trophy trophy, Instant earnedAt) {
        return new TrophyWithStatus(trophy, true, earnedAt);
    }

    public static TrophyWithStatus locked(Trophy trophy) {
        return new TrophyWithStatus(trophy, false, null);
    }
}
