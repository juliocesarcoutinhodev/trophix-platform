package com.trophix.api.shared.exception;

/**
 * Thrown when the PSN sidecar (downstream adapter) is unreachable or fails.
 * Message is a user-facing Brazilian Portuguese description.
 */
public class PsnServiceException extends ApiException {

    public PsnServiceException(String message) {
        super(message);
    }
}