package com.trophix.api.settings.infrastructure.adapter.out;

import com.trophix.api.settings.application.ports.out.GlobalSettingsRepository;
import com.trophix.api.settings.model.GlobalSettings;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class GlobalSettingsJpaAdapter implements GlobalSettingsRepository {

    private final GlobalSettingsSpringDataRepository springDataRepository;
    private final GlobalSettingsMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<GlobalSettings> find() {
        return springDataRepository.findById(GlobalSettings.SINGLE_ROW_ID).map(mapper::toDomain);
    }

    @Override
    @Transactional
    public GlobalSettings save(GlobalSettings settings) {
        return mapper.toDomain(springDataRepository.save(mapper.toEntity(settings)));
    }
}
