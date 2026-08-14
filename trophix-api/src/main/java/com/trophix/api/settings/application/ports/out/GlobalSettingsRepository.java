package com.trophix.api.settings.application.ports.out;

import com.trophix.api.settings.model.GlobalSettings;

import java.util.Optional;

/**
 * Persistence contract for the single global settings row.
 */
public interface GlobalSettingsRepository {

    Optional<GlobalSettings> find();

    GlobalSettings save(GlobalSettings settings);
}
