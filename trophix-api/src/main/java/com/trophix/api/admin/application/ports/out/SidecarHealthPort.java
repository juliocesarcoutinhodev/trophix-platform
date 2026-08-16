package com.trophix.api.admin.application.ports.out;

/**
 * Liveness probe for the PSN sidecar used by the admin overview.
 */
public interface SidecarHealthPort {

    /** {@code true} when the sidecar answers its health route successfully. */
    boolean isUp();
}
