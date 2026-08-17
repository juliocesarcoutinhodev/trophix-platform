package com.trophix.api.admin.application.usecases;

import com.trophix.api.admin.application.ports.in.UpdateUserRolesUseCase;
import com.trophix.api.auth.application.ports.out.RefreshTokenRepository;
import com.trophix.api.shared.application.ports.out.RoleRepositoryPort;
import com.trophix.api.shared.model.Role;
import com.trophix.api.shared.exception.BusinessException;
import com.trophix.api.shared.exception.ResourceNotFoundException;
import com.trophix.api.users.application.ports.out.UserRepository;
import com.trophix.api.users.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.Set;

@Component
@Slf4j
@RequiredArgsConstructor
public class UpdateUserRolesUseCaseImpl implements UpdateUserRolesUseCase {

    private final UserRepository userRepository;
    private final RoleRepositoryPort roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    @Transactional
    public User updateRoles(UpdateUserRolesCommand command) {
        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        Set<Role> roles = new LinkedHashSet<>();
        for (String roleName : command.roles()) {
            roles.add(roleRepository.findByName(roleName)
                    .orElseThrow(() -> new BusinessException("Cargo " + roleName + " não encontrado.")));
        }

        User updated = userRepository.save(user.withRoles(roles));
        int revoked = refreshTokenRepository.revokeAllForUser(user.id());

        log.info("Cargos atualizados pelo admin: userId={} roles={} sessõesRevogadas={}",
                user.id(), roles.stream().map(Role::name).toList(), revoked);
        return updated;
    }
}
