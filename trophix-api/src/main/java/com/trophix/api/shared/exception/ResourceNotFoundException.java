package com.trophix.api.shared.exception;

/**
 * Thrown when a requested resource does not exist.
 * Message is a user-facing Brazilian Portuguese description.
 */
public class ResourceNotFoundException extends ApiException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}