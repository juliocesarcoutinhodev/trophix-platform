package com.trophix.api.shared.exception;

/**
 * Thrown when a refresh token is missing, invalid, expired, idle or reused
 * (theft). Mapped to HTTP 401 by the {@link GlobalExceptionHandler}. Message is
 * a user-facing Brazilian Portuguese description.
 */
public class RefreshTokenException extends ApiException {

    public RefreshTokenException(String message) {
        super(message);
    }
}
