package com.trophix.api.admin.application.ports.in;

import com.trophix.api.users.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Paginated listing of every registered user (admin).
 */
public interface ListAdminUsersUseCase {

    Page<User> listUsers(Pageable pageable);
}
