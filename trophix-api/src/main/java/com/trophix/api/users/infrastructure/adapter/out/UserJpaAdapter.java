package com.trophix.api.users.infrastructure.adapter.out;

import com.trophix.api.auth.infrastructure.adapter.out.RoleSpringDataRepository;
import com.trophix.api.auth.model.Role;
import com.trophix.api.users.application.ports.out.UserRepository;
import com.trophix.api.users.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class UserJpaAdapter implements UserRepository {

    private final UserSpringDataRepository springDataRepository;
    private final RoleSpringDataRepository roleSpringDataRepository;
    private final UserMapper mapper;

    @Override
    public Optional<User> findById(UUID userId) {
        return springDataRepository.findById(userId).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return springDataRepository.findByUsername(username).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return springDataRepository.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    @Transactional
    public User save(User user) {
        UserJpaEntity entity = springDataRepository.findById(user.id())
                .map(existing -> {
                    // Update existing managed entity (avoids detached entity issues)
                    existing.setUsername(user.username());
                    existing.setEmail(user.email());
                    existing.setPassword(user.password());
                    existing.setAvatarUrl(user.avatarUrl());
                    existing.setAccountId(user.accountId());
                    existing.setPsnLevel(user.psnLevel());
                    existing.setLevelProgress(user.levelProgress());
                    existing.setTotalPlatinum(user.totalPlatinum());
                    existing.setTotalGold(user.totalGold());
                    existing.setTotalSilver(user.totalSilver());
                    existing.setTotalBronze(user.totalBronze());
                    existing.setLastSyncedAt(user.lastSyncedAt());
                    setManagedRoles(existing, user.roles());
                    return existing;
                })
                .orElseGet(() -> {
                    // New entity
                    UserJpaEntity created = mapper.toEntity(user);
                    setManagedRoles(created, user.roles());
                    return created;
                });

        return mapper.toDomain(springDataRepository.save(entity));
    }

    @Override
    @Transactional
    public void updateLastSyncedAt(UUID userId, Instant lastSyncedAt) {
        springDataRepository.updateLastSyncedAt(userId, lastSyncedAt);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UUID> findActiveUserIds(Instant since) {
        return springDataRepository.findActiveUserIds(since);
    }

    /**
     * Fetches managed role references from the DB and sets them on the entity.
     * This is required to avoid Hibernate's "detached entity passed to persist" error
     * when associating existing roles via the @ManyToMany join table.
     */
    private void setManagedRoles(UserJpaEntity entity, Set<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            entity.getRoles().clear();
            return;
        }
        Set<UUID> roleIds = roles.stream().map(Role::id).collect(Collectors.toSet());
        entity.setRoles(roleSpringDataRepository.findAllById(roleIds)
                .stream()
                .collect(Collectors.toCollection(java.util.HashSet::new)));
    }
}