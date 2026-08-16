package com.trophix.api.admin.infrastructure.adapter.in.dto;

/**
 * Availability of the PSN sidecar as seen by the admin dashboard.
 * The HTTP status code itself conveys online (200) vs offline (503).
 */
public record SidecarStatusResponse(String status) {
}
