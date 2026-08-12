package com.trophix.api.users.infrastructure.adapter.in;

import com.trophix.api.users.application.ports.in.SyncUserProfileUseCase;
import com.trophix.api.users.infrastructure.adapter.in.dto.SyncProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserProfileController {

    private final SyncUserProfileUseCase syncUserProfileUseCase;

    @PostMapping("/me/sync")
    public ResponseEntity<SyncProfileResponse> sync(@AuthenticationPrincipal String userId) {
        String message = syncUserProfileUseCase.sync(UUID.fromString(userId));
        return ResponseEntity.ok(new SyncProfileResponse(message));
    }
}