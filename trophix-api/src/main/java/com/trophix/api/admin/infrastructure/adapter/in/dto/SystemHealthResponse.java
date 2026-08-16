package com.trophix.api.admin.infrastructure.adapter.in.dto;

/**
 * Liveness of the core API itself. The 200 status code already conveys
 * "online"; the body is informational.
 */
public record SystemHealthResponse(String status) {
}
