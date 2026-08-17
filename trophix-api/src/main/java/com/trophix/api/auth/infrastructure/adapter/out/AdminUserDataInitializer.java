package com.trophix.api.auth.infrastructure.adapter.out;

import com.trophix.api.auth.application.ports.out.PasswordEncoderPort;
import com.trophix.api.shared.application.ports.out.RoleRepositoryPort;
import com.trophix.api.shared.model.Role;
import com.trophix.api.shared.domain.UuidV7;
import com.trophix.api.users.application.ports.out.UserRepository;
import com.trophix.api.users.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Seeds an administrator user on startup. Idempotent: skipped when the
 * admin email already exists. Credentials come from configuration
 * (profile-specific, overrideable by environment variables).
 */
@Component
@Order(2)
@Slf4j
public class AdminUserDataInitializer implements ApplicationRunner {

    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    private final UserRepository userRepository;
    private final RoleRepositoryPort roleRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final String adminEmail;
    private final String adminPassword;
    private final String adminPsnId;

    public AdminUserDataInitializer(
            UserRepository userRepository,
            RoleRepositoryPort roleRepository,
            PasswordEncoderPort passwordEncoder,
            @Value("${trophix.admin.email:}") String adminEmail,
            @Value("${trophix.admin.password:}") String adminPassword,
            @Value("${trophix.admin.psn-id:}") String adminPsnId) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
        this.adminPsnId = adminPsnId;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (adminEmail.isBlank() || adminPassword.isBlank() || adminPsnId.isBlank()) {
            log.warn("Seed de admin ignorado: configure trophix.admin.email/password/psn-id.");
            return;
        }

        if (userRepository.findByEmail(adminEmail).isPresent()) {
            log.debug("Usuário admin já existe, seed ignorado: {}", adminEmail);
            return;
        }

        Role adminRole = roleRepository.findByName(ROLE_ADMIN)
                .orElseThrow(() -> new IllegalStateException(
                        "Role " + ROLE_ADMIN + " não encontrada no seed de roles."));

        User admin = new User(
                UuidV7.generate(),
                adminPsnId,
                adminEmail,
                passwordEncoder.encode(adminPassword),
                null,
                Set.of(adminRole),
                null, null, null, null, null, null, null, null);

        userRepository.save(admin);
        log.info("Usuário administrador criado: {}", adminEmail);
    }
}