package com.trophix.api.users.infrastructure.adapter.out;

import com.trophix.api.auth.infrastructure.adapter.out.RoleJpaEntity;
import com.trophix.api.auth.model.Role;
import com.trophix.api.users.model.User;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public User toDomain(UserJpaEntity entity) {
        Set<Role> roles = entity.getRoles().stream()
                .map(r -> new Role(r.getId(), r.getName()))
                .collect(Collectors.toSet());

        return new User(
                entity.getId(),
                entity.getUsername(),
                entity.getEmail(),
                entity.getPassword(),
                entity.getAvatarUrl(),
                roles,
                entity.getAccountId(),
                entity.getPsnLevel(),
                entity.getLevelProgress(),
                entity.getTotalPlatinum(),
                entity.getTotalGold(),
                entity.getTotalSilver(),
                entity.getTotalBronze(),
                entity.getLastSyncedAt());
    }

    /**
     * Converts a domain User to a JPA entity.
     * NOTE: roles are NOT set here — the adapter sets managed role references
     * via RoleSpringDataRepository to avoid detached-entity issues with Hibernate.
     */
    public UserJpaEntity toEntity(User user) {
        UserJpaEntity entity = new UserJpaEntity();
        entity.setId(user.id());
        entity.setUsername(user.username());
        entity.setEmail(user.email());
        entity.setPassword(user.password());
        entity.setAvatarUrl(user.avatarUrl());
        entity.setAccountId(user.accountId());
        entity.setPsnLevel(user.psnLevel());
        entity.setLevelProgress(user.levelProgress());
        entity.setTotalPlatinum(user.totalPlatinum());
        entity.setTotalGold(user.totalGold());
        entity.setTotalSilver(user.totalSilver());
        entity.setTotalBronze(user.totalBronze());
        entity.setLastSyncedAt(user.lastSyncedAt());
        return entity;
    }

    public RoleJpaEntity toRoleEntity(Role role) {
        RoleJpaEntity entity = new RoleJpaEntity();
        entity.setId(role.id());
        entity.setName(role.name());
        return entity;
    }
}