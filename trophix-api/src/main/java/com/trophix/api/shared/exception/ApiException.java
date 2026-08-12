package com.trophix.api.shared.exception;

/**
 * Base exception for all business errors of the API.
 * Subclasses must provide user-facing messages in Brazilian Portuguese.
 */
public abstract class ApiException extends RuntimeException {

    protected ApiException(String message) {
        super(message);
    }
}