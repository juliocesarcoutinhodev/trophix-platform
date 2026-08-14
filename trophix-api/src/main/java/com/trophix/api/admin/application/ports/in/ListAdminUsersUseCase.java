package com.trophix.api.admin.application.ports.in;

import com.trophix.api.users.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Paginated listing of registered users (admin), optionally filtered by a
 * free-text search (username/email) and/or a specific role.
 */
public interface ListAdminUsersUseCase {

    Page<User> listUsers(String search, String role, Pageable pageable);
}
