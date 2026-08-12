package com.trophix.api.shared.exception;

/**
 * Thrown when a manual sync is requested before the cooldown elapses.
 * Message is a user-facing Brazilian Portuguese description.
 */
public class SyncCooldownException extends ApiException {

    private final long minutesRemaining;

    public SyncCooldownException(String message, long minutesRemaining) {
        super(message);
        this.minutesRemaining = minutesRemaining;
    }

    public long getMinutesRemaining() {
        return minutesRemaining;
    }
}