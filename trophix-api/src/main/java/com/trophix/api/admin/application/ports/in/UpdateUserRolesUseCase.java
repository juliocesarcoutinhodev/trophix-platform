package com.trophix.api.admin.application.ports.in;

import com.trophix.api.users.model.User;

import java.util.Set;
import java.util.UUID;

/**
 * Replaces the roles of a user (admin). Existing sessions are revoked so the
 * user re-authenticates and receives a token carrying the new roles.
 */
public interface UpdateUserRolesUseCase {

    User updateRoles(UpdateUserRolesCommand command);

    record UpdateUserRolesCommand(UUID userId, Set<String> roles) {
    }
}
