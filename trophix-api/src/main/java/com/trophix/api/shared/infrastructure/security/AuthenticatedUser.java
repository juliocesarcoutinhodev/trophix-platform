package com.trophix.api.shared.infrastructure.security;

import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Resolves the authenticated user id from the JWT principal. Public endpoints
 * may receive an absent principal or the anonymous placeholder, in which case
 * an empty Optional is returned (the caller treats it as "not logged in").
 */
@Component
public class AuthenticatedUser {

    public Optional<UUID> optionalId(String principal) {
        if (principal == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(UUID.fromString(principal));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }
}
