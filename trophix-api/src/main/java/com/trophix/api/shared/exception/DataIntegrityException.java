package com.trophix.api.shared.exception;

/**
 * Thrown when a required system resource is missing (e.g. a seeded Role not found).
 * Indicates a data integrity / setup problem, not a user error.
 * Message is user-facing Brazilian Portuguese.
 */
public class DataIntegrityException extends ApiException {

    public DataIntegrityException(String message) {
        super(message);
    }
}
