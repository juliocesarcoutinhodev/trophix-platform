package com.trophix.api.settings.application.ports.in;

import com.trophix.api.settings.model.GlobalSettings;

/**
 * Returns the current global settings, falling back to defaults when the row
 * does not exist yet.
 */
public interface GetGlobalSettingsUseCase {

    GlobalSettings get();
}
