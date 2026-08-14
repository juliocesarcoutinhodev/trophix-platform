package com.trophix.api.auth.infrastructure.adapter.out;

import com.trophix.api.auth.application.ports.out.RoleRepositoryPort;
import com.trophix.api.auth.model.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostgresRoleAdapter implements RoleRepositoryPort {

    private final RoleSpringDataRepository springDataRepository;

    @Override
    public Optional<Role> findByName(String name) {
        return springDataRepository.findByName(name)
                .map(entity -> new Role(entity.getId(), entity.getName()));
    }

    @Override
    public List<Role> findAll() {
        return springDataRepository.findAll().stream()
                .map(entity -> new Role(entity.getId(), entity.getName()))
                .toList();
    }
}
