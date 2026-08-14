package com.trophix.api.admin.application.usecases;

import com.trophix.api.admin.application.ports.in.ListAdminUsersUseCase;
import com.trophix.api.users.application.ports.out.UserRepository;
import com.trophix.api.users.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ListAdminUsersUseCaseImpl implements ListAdminUsersUseCase {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<User> listUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
}
