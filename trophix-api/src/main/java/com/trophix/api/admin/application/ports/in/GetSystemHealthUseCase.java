package com.trophix.api.admin.application.ports.in;

/**
 * Ultra-light liveness probe for the core API itself. Performs no external
 * checks — reaching this endpoint already proves the API is up.
 */
public interface GetSystemHealthUseCase {

    SystemHealth getHealth();

    record SystemHealth(boolean up) {
    }
}
