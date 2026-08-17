package com.trophix.api.shared.application.ports.out;

import com.trophix.api.shared.model.Role;

import java.util.List;
import java.util.Optional;

public interface RoleRepositoryPort {

    Optional<Role> findByName(String name);

    List<Role> findAll();
}
