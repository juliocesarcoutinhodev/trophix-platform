package com.trophix.api.users.infrastructure.adapter.in.mapper;

import com.trophix.api.auth.infrastructure.adapter.in.dto.LoginRequest;
import com.trophix.api.auth.infrastructure.adapter.in.dto.RegistrationRequest;
import com.trophix.api.auth.infrastructure.adapter.in.dto.UserResponse;
import com.trophix.api.auth.model.Role;
import com.trophix.api.games.model.UserGameSummary;
import com.trophix.api.users.infrastructure.adapter.in.dto.AccountLinkTokenResponse;
import com.trophix.api.users.infrastructure.adapter.in.dto.AccountLinkValidationResponse;
import com.trophix.api.users.infrastructure.adapter.in.dto.UserGameResponse;
import com.trophix.api.users.infrastructure.adapter.in.dto.UserProfileResponse;
import com.trophix.api.users.model.AccountLinkValidation;
import com.trophix.api.users.model.User;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Converts inbound web DTOs into application responses, keeping the
 * controllers free of manual instantiation and mapping logic.
 */
@Component
public class UserWebMapper {

    public AccountLinkTokenResponse toTokenResponse(String token) {
        return new AccountLinkTokenResponse(token);
    }

    public AccountLinkValidationResponse toValidationResponse(AccountLinkValidation validation) {
        return new AccountLinkValidationResponse(
                validation.userId().toString(),
                validation.psnId(),
                validation.message());
    }

    public UserProfileResponse toUserProfileResponse(User user) {
        return new UserProfileResponse(
                user.username(),
                user.avatarUrl(),
                user.psnLevel(),
                user.levelProgress(),
                user.totalPlatinum(),
                user.totalGold(),
                user.totalSilver(),
                user.totalBronze());
    }

    public UserGameResponse toUserGameResponse(UserGameSummary summary) {
        return new UserGameResponse(
                summary.gameId(),
                summary.name(),
                summary.imageUrl(),
                summary.platform(),
                summary.progressPercentage(),
                summary.earnedTrophies(),
                summary.totalTrophies(),
                summary.lastPlayedAt());
    }
}