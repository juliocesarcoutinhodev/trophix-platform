package com.trophix.api.settings.infrastructure.adapter.in;

import com.trophix.api.settings.application.ports.in.GetGlobalSettingsUseCase;
import com.trophix.api.settings.application.ports.in.UpdateGlobalSettingsUseCase;
import com.trophix.api.settings.infrastructure.adapter.in.dto.GlobalSettingsResponse;
import com.trophix.api.settings.infrastructure.adapter.in.mapper.GlobalSettingsWebMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Global settings admin endpoints. Protected by the security rule
 * {@code /api/admin/**} (ROLE_ADMIN); the controller only orchestrates use cases.
 */
@RestController
@RequestMapping("/api/admin/settings")
@RequiredArgsConstructor
public class GlobalSettingsController {

    private final GetGlobalSettingsUseCase getGlobalSettingsUseCase;
    private final UpdateGlobalSettingsUseCase updateGlobalSettingsUseCase;
    private final GlobalSettingsWebMapper webMapper;

    @GetMapping
    public ResponseEntity<GlobalSettingsResponse> getSettings() {
        return ResponseEntity.ok(webMapper.toResponse(getGlobalSettingsUseCase.get()));
    }

    @PutMapping
    public ResponseEntity<GlobalSettingsResponse> updateSettings(
            @Valid @RequestBody GlobalSettingsResponse request) {
        GlobalSettingsResponse saved = webMapper.toResponse(
                updateGlobalSettingsUseCase.update(webMapper.toCommand(request)));
        return ResponseEntity.ok(saved);
    }
}
