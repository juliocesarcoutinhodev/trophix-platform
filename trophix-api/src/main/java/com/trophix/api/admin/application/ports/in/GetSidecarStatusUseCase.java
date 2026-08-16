package com.trophix.api.admin.application.ports.in;

/**
 * Probes the PSN sidecar so the admin dashboard can show its availability.
 */
public interface GetSidecarStatusUseCase {

    SidecarStatus getStatus();

    record SidecarStatus(boolean up) {
    }
}
