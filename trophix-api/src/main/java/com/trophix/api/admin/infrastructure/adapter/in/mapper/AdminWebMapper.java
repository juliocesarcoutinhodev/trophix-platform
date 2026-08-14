package com.trophix.api.admin.infrastructure.adapter.in.mapper;

import com.trophix.api.admin.application.ports.in.GetDashboardStatsUseCase;
import com.trophix.api.admin.infrastructure.adapter.in.dto.AdminUserResponse;
import com.trophix.api.admin.infrastructure.adapter.in.dto.DashboardStatsResponse;
import com.trophix.api.admin.infrastructure.adapter.in.dto.ModerationGuideResponse;
import com.trophix.api.auth.model.Role;
import com.trophix.api.guides.model.Guide;
import com.trophix.api.guides.model.GuideListItem;
import com.trophix.api.users.model.User;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Converts admin application results into web responses, keeping the
 * controller free of manual instantiation.
 */
@Component
public class AdminWebMapper {

    public DashboardStatsResponse toDashboardStatsResponse(GetDashboardStatsUseCase.DashboardStats stats) {
        return new DashboardStatsResponse(
                stats.newUsersToday(),
                stats.pendingGuides(),
                stats.openReports());
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
}
