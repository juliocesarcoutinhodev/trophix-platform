package com.trophix.api.auth.application.ports.out;

import com.trophix.api.auth.model.Role;

import java.util.List;
import java.util.Optional;

public interface RoleRepositoryPort {

    Optional<Role> findByName(String name);

    List<Role> findAll();
}
