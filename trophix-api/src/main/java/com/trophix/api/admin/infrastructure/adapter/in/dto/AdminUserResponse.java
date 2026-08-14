package com.trophix.api.admin.infrastructure.adapter.in.dto;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

/**
 * User summary used by the admin user management.
 */
public record AdminUserResponse(
        UUID id,
        String username,
        String email,
        String avatarUrl,
        Set<String> roles,
        String accountId,
        Integer psnLevel,
        Instant lastSyncedAt) {
}
