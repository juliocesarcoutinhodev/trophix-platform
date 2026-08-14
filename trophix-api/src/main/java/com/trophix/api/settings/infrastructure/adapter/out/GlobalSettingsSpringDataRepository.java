package com.trophix.api.settings.infrastructure.adapter.out;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GlobalSettingsSpringDataRepository extends JpaRepository<GlobalSettingsJpaEntity, UUID> {
}
