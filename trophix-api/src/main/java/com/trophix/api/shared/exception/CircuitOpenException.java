package com.trophix.api.shared.exception;

/**
 * Thrown when the circuit breaker is OPEN and calls are being rejected
 * fast to protect the API from a downstream outage. Message is a
 * user-facing Brazilian Portuguese description.
 */
public class CircuitOpenException extends ApiException {

    public CircuitOpenException(String message) {
        super(message);
    }
}
