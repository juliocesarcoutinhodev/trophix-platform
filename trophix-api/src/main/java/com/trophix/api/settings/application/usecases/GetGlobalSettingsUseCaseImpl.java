package com.trophix.api.settings.application.usecases;

import com.trophix.api.settings.application.ports.in.GetGlobalSettingsUseCase;
import com.trophix.api.settings.application.ports.out.GlobalSettingsRepository;
import com.trophix.api.settings.model.GlobalSettings;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class GetGlobalSettingsUseCaseImpl implements GetGlobalSettingsUseCase {

    private final GlobalSettingsRepository settingsRepository;

    @Override
    @Transactional(readOnly = true)
    public GlobalSettings get() {
        return settingsRepository.find().orElseGet(GlobalSettings::defaults);
    }
}
