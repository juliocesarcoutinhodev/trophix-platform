package com.trophix.api.users.infrastructure.adapter.in;

import com.trophix.api.games.model.UserGameSummary;
import com.trophix.api.users.application.ports.in.GetUserGamesUseCase;
import com.trophix.api.users.application.ports.in.GetUserProfileUseCase;
import com.trophix.api.users.application.ports.in.SyncUserProfileUseCase;
import com.trophix.api.users.infrastructure.adapter.in.dto.SyncProfileResponse;
import com.trophix.api.users.infrastructure.adapter.in.dto.UserGameResponse;
import com.trophix.api.users.infrastructure.adapter.in.dto.UserProfileResponse;
import com.trophix.api.users.infrastructure.adapter.in.mapper.UserWebMapper;
import com.trophix.api.users.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserProfileController {

    private static final int DEFAULT_PAGE_SIZE = 20;

    private final GetUserProfileUseCase getUserProfileUseCase;
    private final GetUserGamesUseCase getUserGamesUseCase;
    private final SyncUserProfileUseCase syncUserProfileUseCase;
    private final UserWebMapper userWebMapper;

    @PostMapping("/me/sync")
    public ResponseEntity<SyncProfileResponse> sync(@AuthenticationPrincipal String userId) {
        syncUserProfileUseCase.requestSync(UUID.fromString(userId));
        return ResponseEntity.accepted().body(new SyncProfileResponse(
                "Sincronização iniciada. Os dados serão atualizados em segundo plano."));
    }

    @GetMapping("/me/profile")
    public ResponseEntity<UserProfileResponse> getMyProfile(@AuthenticationPrincipal String userId) {
        User user = getUserProfileUseCase.getProfileByUserId(UUID.fromString(userId));
        return ResponseEntity.ok(userWebMapper.toUserProfileResponse(user));
    }

    @GetMapping("/me/games")
    public ResponseEntity<Page<UserGameResponse>> getMyGames(
            @AuthenticationPrincipal String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        Page<UserGameSummary> games = getUserGamesUseCase.getGamesByUserId(
                UUID.fromString(userId), PageRequest.of(Math.max(page, 0), Math.min(size, 100)));
        return ResponseEntity.ok(games.map(userWebMapper::toUserGameResponse));
    }

    @GetMapping("/{username}/profile")
    public ResponseEntity<UserProfileResponse> getProfile(@PathVariable String username) {
        User user = getUserProfileUseCase.getProfile(username);
        return ResponseEntity.ok(userWebMapper.toUserProfileResponse(user));
    }

    @GetMapping("/{username}/games")
    public ResponseEntity<Page<UserGameResponse>> getGames(
            @PathVariable String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<UserGameSummary> games = getUserGamesUseCase.getGames(
                username, PageRequest.of(Math.max(page, 0), Math.min(size, 100)));
        return ResponseEntity.ok(games.map(userWebMapper::toUserGameResponse));
    }
}