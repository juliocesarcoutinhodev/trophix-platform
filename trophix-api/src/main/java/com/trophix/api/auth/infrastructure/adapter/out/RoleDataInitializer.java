package com.trophix.api.auth.infrastructure.adapter.out;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Seeds the required roles on startup. Idempotent: roles already present
 * are kept untouched.
 */
@Component
@Order(1)
@Slf4j
@RequiredArgsConstructor
public class RoleDataInitializer implements ApplicationRunner {

    private static final List<String> DEFAULT_ROLES = List.of("ROLE_USER", "ROLE_ADMIN");

    private final RoleSpringDataRepository roleRepository;

    @Override
    public void run(ApplicationArguments args) {
        for (String roleName : DEFAULT_ROLES) {
            if (roleRepository.findByName(roleName).isEmpty()) {
                RoleJpaEntity role = new RoleJpaEntity();
                role.setName(roleName);
                roleRepository.save(role);
                log.info("Role criada no boot: {}", roleName);
            }
        }
    }
}