package com.trophix.api.admin.infrastructure.adapter.in.mapper;

import com.trophix.api.admin.application.ports.in.GetDashboardStatsUseCase;
import com.trophix.api.admin.application.ports.in.GetSidecarStatusUseCase;
import com.trophix.api.admin.application.ports.in.GetSystemHealthUseCase;
import com.trophix.api.admin.application.ports.in.SetGameFeaturedUseCase;
import com.trophix.api.admin.application.ports.in.UpdateGuideUseCase;
import com.trophix.api.admin.application.ports.in.UpdateUserRolesUseCase;
import com.trophix.api.admin.infrastructure.adapter.in.dto.AdminDashboardStatsResponse;
import com.trophix.api.admin.infrastructure.adapter.in.dto.AdminUserResponse;
import com.trophix.api.admin.infrastructure.adapter.in.dto.ModerationGuideResponse;
import com.trophix.api.admin.infrastructure.adapter.in.dto.SidecarStatusResponse;
import com.trophix.api.admin.infrastructure.adapter.in.dto.SystemHealthResponse;
import com.trophix.api.admin.infrastructure.adapter.in.dto.UpdateGameFeaturedRequest;
import com.trophix.api.admin.infrastructure.adapter.in.dto.UpdateGuideRequest;
import com.trophix.api.admin.infrastructure.adapter.in.dto.UpdateUserRolesRequest;
import com.trophix.api.games.application.ports.in.ImportGameUseCase;
import com.trophix.api.shared.dto.MessageResponse;
import com.trophix.api.shared.model.Role;
import com.trophix.api.guides.model.Guide;
import com.trophix.api.guides.model.GuideListItem;
import com.trophix.api.users.model.User;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Converts admin application results into web responses, keeping the
 * controller free of manual instantiation.
 */
@Component
public class AdminWebMapper {

    public AdminDashboardStatsResponse toDashboardStatsResponse(GetDashboardStatsUseCase.DashboardStats stats) {
        return new AdminDashboardStatsResponse(
                stats.newUsersCount(),
                stats.newUsersTrend(),
                "desde ontem",
                stats.pendingGuidesCount(),
                stats.pendingGuidesTrend(),
                "aguardando revisão",
                stats.syncsCount(),
                "nas últimas 24h",
                stats.syncsTrendPositive(),
                stats.reportsCount(),
                stats.reportsTrend(),
                "desde ontem");
    }

    public AdminUserResponse toAdminUserResponse(User user) {
        return new AdminUserResponse(
                user.id(),
                user.username(),
                user.email(),
                user.avatarUrl(),
                user.roles().stream().map(Role::name).collect(Collectors.toSet()),
                user.accountId(),
                user.psnLevel(),
                user.lastSyncedAt());
    }

    public ModerationGuideResponse toModerationGuideResponse(GuideListItem item) {
        Guide guide = item.guide();
        return new ModerationGuideResponse(
                guide.id(),
                guide.trophyId(),
                guide.gameId(),
                item.gameName(),
                item.imageUrl(),
                guide.authorId(),
                item.authorName(),
                guide.title(),
                guide.description(),
                guide.content(),
                guide.videoUrl(),
                guide.status().name(),
                guide.upvotesCount(),
                guide.createdAt(),
                guide.updatedAt());
    }

    public UpdateGuideUseCase.UpdateGuideCommand toUpdateGuideCommand(UUID guideId, UpdateGuideRequest request) {
        return new UpdateGuideUseCase.UpdateGuideCommand(
                guideId, request.title(), request.description(), request.content(), request.videoUrl());
    }

    public SetGameFeaturedUseCase.SetGameFeaturedCommand toSetGameFeaturedCommand(
            UUID gameId, UpdateGameFeaturedRequest request) {
        return new SetGameFeaturedUseCase.SetGameFeaturedCommand(gameId, request.isFeatured());
    }

    public UpdateUserRolesUseCase.UpdateUserRolesCommand toUpdateUserRolesCommand(
            UUID userId, UpdateUserRolesRequest request) {
        return new UpdateUserRolesUseCase.UpdateUserRolesCommand(userId, request.roles());
    }

    public ImportGameUseCase.ImportGameCommand toImportGameCommand(String npCommunicationId, UUID adminId) {
        return new ImportGameUseCase.ImportGameCommand(npCommunicationId, adminId);
    }

    public MessageResponse toMessageResponse(String message) {
        return new MessageResponse(message);
    }

    public SidecarStatusResponse toSidecarStatusResponse(GetSidecarStatusUseCase.SidecarStatus status) {
        return new SidecarStatusResponse(status.up() ? "UP" : "DOWN");
    }

    public SystemHealthResponse toSystemHealthResponse(GetSystemHealthUseCase.SystemHealth health) {
        return new SystemHealthResponse(health.up() ? "UP" : "DOWN");
    }
}
