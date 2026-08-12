package com.trophix.api.users.model;

import com.trophix.api.auth.model.Role;
import com.trophix.api.shared.domain.UuidV7;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

/**
 * User domain entity. Pure Java, no framework annotations.
 * <p>
 * Email and password are optional for PSN-only accounts — filled in
 * during {@code CompleteRegistrationUseCase}.
 * Roles are empty until registration is completed.
 * PSN profile fields (accountId, level and trophy totals) are populated
 * during the profile synchronization flow.
 * lastSyncedAt controls the manual sync cooldown.
 */
public record User(
        UUID id,
        String username,
        String email,
        String password,
        String avatarUrl,
        Set<Role> roles,
        String accountId,
        Integer psnLevel,
        Integer levelProgress,
        Integer totalPlatinum,
        Integer totalGold,
        Integer totalSilver,
        Integer totalBronze,
        Instant lastSyncedAt) {

    public static User create(String username, String email, String password, String avatarUrl) {
        return new User(UuidV7.generate(), username, email, password, avatarUrl, Set.of(),
                null, null, null, null, null, null, null, null);
    }

    /** Creates a PSN-linked user without credentials. Registration must be completed separately. */
    public static User createFromPsn(String psnId, String avatarUrl) {
        return new User(UuidV7.generate(), psnId, null, null, avatarUrl, Set.of(),
                null, null, null, null, null, null, null, null);
    }
}