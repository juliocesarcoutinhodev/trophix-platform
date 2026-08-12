package com.trophix.api.shared.exception;

/**
 * Thrown when a business rule is violated (bad state, invalid flow).
 * Message is a user-facing Brazilian Portuguese description.
 */
public class BusinessException extends ApiException {

    public BusinessException(String message) {
        super(message);
    }
}