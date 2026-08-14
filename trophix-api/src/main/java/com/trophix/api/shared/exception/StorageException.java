package com.trophix.api.shared.exception;

/**
 * Thrown when an object cannot be stored/retrieved from the object storage
 * (MinIO/S3). Mapped to HTTP 500 by the {@link GlobalExceptionHandler}.
 * Message is a user-facing Brazilian Portuguese description.
 */
public class StorageException extends ApiException {

    public StorageException(String message) {
        super(message);
    }
}
