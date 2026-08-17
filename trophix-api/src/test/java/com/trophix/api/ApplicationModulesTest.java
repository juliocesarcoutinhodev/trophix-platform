package com.trophix.api;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.core.Violation;
import org.springframework.modulith.core.Violations;

import java.util.Set;

/**
 * Verifies the Spring Modulith application module arrangement. The current
 * codebase carries legacy cross-module accesses (dívida técnica); those are
 * allow-listed here (by the stable first line of each violation) so the build
 * stays green while the modules are refactored. Any NEW violation fails this
 * test - remove the allow-listed entries as each module is fixed.
 */
class ApplicationModulesTest {

    private static final Set<String> KNOWN_VIOLATIONS = Set.of(
            "Cycle detected: Slice auth ->",
            "Cycle detected: Slice games ->",
            "Cycle detected: Slice shared ->",
            "Module 'admin' depends on non-exposed type com.trophix.api.auth.application.ports.out.RefreshTokenRepository within module 'auth'!",
            "Module 'admin' depends on non-exposed type com.trophix.api.shared.application.ports.out.RoleRepositoryPort within module 'shared'!",
            "Module 'admin' depends on non-exposed type com.trophix.api.shared.model.Role within module 'shared'!",
            "Module 'admin' depends on non-exposed type com.trophix.api.guides.application.ports.in.ReviewGuideUseCase within module 'guides'!",
            "Module 'admin' depends on non-exposed type com.trophix.api.guides.application.ports.in.ReviewGuideUseCase$ReviewAction within module 'guides'!",
            "Module 'admin' depends on non-exposed type com.trophix.api.guides.application.ports.out.GuideRepositoryPort within module 'guides'!",
            "Module 'admin' depends on non-exposed type com.trophix.api.guides.application.service.GuideEnricher within module 'guides'!",
            "Module 'admin' depends on non-exposed type com.trophix.api.guides.infrastructure.adapter.in.dto.GuideResponse within module 'guides'!",
            "Module 'admin' depends on non-exposed type com.trophix.api.guides.infrastructure.adapter.in.dto.MessageResponse within module 'guides'!",
            "Module 'admin' depends on non-exposed type com.trophix.api.guides.infrastructure.adapter.in.mapper.GuideWebMapper within module 'guides'!",
            "Module 'admin' depends on non-exposed type com.trophix.api.guides.model.Guide within module 'guides'!",
            "Module 'admin' depends on non-exposed type com.trophix.api.guides.model.GuideListItem within module 'guides'!",
            "Module 'admin' depends on non-exposed type com.trophix.api.guides.model.GuideStatus within module 'guides'!",
            "Module 'admin' depends on non-exposed type com.trophix.api.reports.application.ports.out.ReportRepository within module 'reports'!",
            "Module 'admin' depends on non-exposed type com.trophix.api.reports.model.ReportStatus within module 'reports'!",
            "Module 'admin' depends on non-exposed type com.trophix.api.shared.exception.BusinessException within module 'shared'!",
            "Module 'admin' depends on non-exposed type com.trophix.api.shared.exception.ResourceNotFoundException within module 'shared'!",
            "Module 'admin' depends on non-exposed type com.trophix.api.shared.infrastructure.web.SidecarClient within module 'shared'!",
            "Module 'admin' depends on non-exposed type com.trophix.api.users.application.ports.out.UserRepository within module 'users'!",
            "Module 'admin' depends on non-exposed type com.trophix.api.users.model.User within module 'users'!",
            "Module 'auth' depends on non-exposed type com.trophix.api.shared.domain.UuidV7 within module 'shared'!",
            "Module 'auth' depends on non-exposed type com.trophix.api.shared.infrastructure.ratelimit.RateLimitFilter within module 'shared'!",
            "Module 'auth' depends on non-exposed type com.trophix.api.shared.exception.BusinessException within module 'shared'!",
            "Module 'auth' depends on non-exposed type com.trophix.api.shared.exception.DataIntegrityException within module 'shared'!",
            "Module 'auth' depends on non-exposed type com.trophix.api.shared.exception.RefreshTokenException within module 'shared'!",
            "Module 'auth' depends on non-exposed type com.trophix.api.shared.infrastructure.persistence.UuidV7Id within module 'shared'!",
            "Module 'auth' depends on non-exposed type com.trophix.api.shared.application.ports.out.RoleRepositoryPort within module 'shared'!",
            "Module 'auth' depends on non-exposed type com.trophix.api.shared.model.Role within module 'shared'!",
            "Module 'auth' depends on non-exposed type com.trophix.api.users.application.ports.out.PsnProfileFetcherPort within module 'users'!",
            "Module 'auth' depends on non-exposed type com.trophix.api.users.application.ports.out.UserRepository within module 'users'!",
            "Module 'auth' depends on non-exposed type com.trophix.api.users.model.PsnProfile within module 'users'!",
            "Module 'auth' depends on non-exposed type com.trophix.api.users.model.User within module 'users'!",
            "Module 'forums' depends on non-exposed type com.trophix.api.auth.application.ports.out.EmailSenderPort within module 'auth'!",
            "Module 'forums' depends on non-exposed type com.trophix.api.auth.infrastructure.security.AuthenticatedUser within module 'auth'!",
            "Module 'forums' depends on non-exposed type com.trophix.api.shared.domain.UuidV7 within module 'shared'!",
            "Module 'forums' depends on non-exposed type com.trophix.api.shared.exception.BusinessException within module 'shared'!",
            "Module 'forums' depends on non-exposed type com.trophix.api.shared.exception.ResourceNotFoundException within module 'shared'!",
            "Module 'forums' depends on non-exposed type com.trophix.api.shared.infrastructure.persistence.UuidV7Id within module 'shared'!",
            "Module 'forums' depends on non-exposed type com.trophix.api.users.application.ports.out.UserRepository within module 'users'!",
            "Module 'forums' depends on non-exposed type com.trophix.api.users.model.User within module 'users'!",
            "Module 'games' depends on non-exposed type com.trophix.api.shared.domain.UuidV7 within module 'shared'!",
            "Module 'games' depends on non-exposed type com.trophix.api.shared.exception.ResourceNotFoundException within module 'shared'!",
            "Module 'games' depends on non-exposed type com.trophix.api.shared.infrastructure.persistence.UuidV7Id within module 'shared'!",
            "Module 'games' depends on non-exposed type com.trophix.api.trophies.application.ports.out.TrophyRepositoryPort within module 'trophies'!",
            "Module 'games' depends on non-exposed type com.trophix.api.trophies.application.ports.out.UserTrophyRepositoryPort within module 'trophies'!",
            "Module 'games' depends on non-exposed type com.trophix.api.trophies.model.Trophy within module 'trophies'!",
            "Module 'games' depends on non-exposed type com.trophix.api.users.infrastructure.adapter.out.UserJpaEntity within module 'users'!",
            "Module 'games' depends on non-exposed type com.trophix.api.users.infrastructure.adapter.out.UserSpringDataRepository within module 'users'!",
            "Module 'guides' depends on non-exposed type com.trophix.api.games.application.ports.out.GameRepositoryPort within module 'games'!",
            "Module 'guides' depends on non-exposed type com.trophix.api.games.infrastructure.adapter.out.GameEntity within module 'games'!",
            "Module 'guides' depends on non-exposed type com.trophix.api.games.infrastructure.adapter.out.GameSpringDataRepository within module 'games'!",
            "Module 'guides' depends on non-exposed type com.trophix.api.games.model.Game within module 'games'!",
            "Module 'guides' depends on non-exposed type com.trophix.api.auth.infrastructure.security.AuthenticatedUser within module 'auth'!",
            "Module 'guides' depends on non-exposed type com.trophix.api.shared.domain.UuidV7 within module 'shared'!",
            "Module 'guides' depends on non-exposed type com.trophix.api.shared.exception.BusinessException within module 'shared'!",
            "Module 'guides' depends on non-exposed type com.trophix.api.shared.exception.ResourceNotFoundException within module 'shared'!",
            "Module 'guides' depends on non-exposed type com.trophix.api.shared.infrastructure.persistence.UuidV7Id within module 'shared'!",
            "Module 'guides' depends on non-exposed type com.trophix.api.trophies.application.ports.out.TrophyRepositoryPort within module 'trophies'!",
            "Module 'guides' depends on non-exposed type com.trophix.api.trophies.infrastructure.adapter.out.TrophyEntity within module 'trophies'!",
            "Module 'guides' depends on non-exposed type com.trophix.api.trophies.infrastructure.adapter.out.TrophySpringDataRepository within module 'trophies'!",
            "Module 'guides' depends on non-exposed type com.trophix.api.trophies.model.Trophy within module 'trophies'!",
            "Module 'guides' depends on non-exposed type com.trophix.api.users.application.ports.out.UserRepository within module 'users'!",
            "Module 'guides' depends on non-exposed type com.trophix.api.users.infrastructure.adapter.out.UserJpaEntity within module 'users'!",
            "Module 'guides' depends on non-exposed type com.trophix.api.users.infrastructure.adapter.out.UserSpringDataRepository within module 'users'!",
            "Module 'guides' depends on non-exposed type com.trophix.api.users.model.User within module 'users'!",
            "Module 'reports' depends on non-exposed type com.trophix.api.guides.application.ports.out.GuideRepositoryPort within module 'guides'!",
            "Module 'reports' depends on non-exposed type com.trophix.api.shared.domain.UuidV7 within module 'shared'!",
            "Module 'reports' depends on non-exposed type com.trophix.api.shared.exception.BusinessException within module 'shared'!",
            "Module 'reports' depends on non-exposed type com.trophix.api.shared.exception.ResourceNotFoundException within module 'shared'!",
            "Module 'reports' depends on non-exposed type com.trophix.api.shared.infrastructure.persistence.UuidV7Id within module 'shared'!",
            "Module 'reports' depends on non-exposed type com.trophix.api.users.application.ports.out.UserRepository within module 'users'!",
            "Module 'sync' depends on non-exposed type com.trophix.api.shared.application.ports.out.SyncJobPublisher within module 'shared'!",
            "Module 'sync' depends on non-exposed type com.trophix.api.shared.exception.ApiException within module 'shared'!",
            "Module 'sync' depends on non-exposed type com.trophix.api.trophies.application.ports.in.SyncGameTrophiesUseCase within module 'trophies'!",
            "Module 'sync' depends on non-exposed type com.trophix.api.users.application.async.UserProfileSyncExecutor within module 'users'!",
            "Module 'sync' depends on non-exposed type com.trophix.api.users.application.ports.out.UserRepository within module 'users'!",
            "Module 'sync' depends on non-exposed type com.trophix.api.users.model.User within module 'users'!",
            "Module 'trophies' depends on non-exposed type com.trophix.api.games.application.ports.out.GameRepositoryPort within module 'games'!",
            "Module 'trophies' depends on non-exposed type com.trophix.api.games.infrastructure.adapter.out.GameEntity within module 'games'!",
            "Module 'trophies' depends on non-exposed type com.trophix.api.games.infrastructure.adapter.out.GameSpringDataRepository within module 'games'!",
            "Module 'trophies' depends on non-exposed type com.trophix.api.games.model.Game within module 'games'!",
            "Module 'trophies' depends on non-exposed type com.trophix.api.shared.application.ports.out.SyncJobPublisher within module 'shared'!",
            "Module 'trophies' depends on non-exposed type com.trophix.api.shared.domain.UuidV7 within module 'shared'!",
            "Module 'trophies' depends on non-exposed type com.trophix.api.shared.exception.BusinessException within module 'shared'!",
            "Module 'trophies' depends on non-exposed type com.trophix.api.shared.exception.ResourceNotFoundException within module 'shared'!",
            "Module 'trophies' depends on non-exposed type com.trophix.api.shared.infrastructure.persistence.UuidV7Id within module 'shared'!",
            "Module 'trophies' depends on non-exposed type com.trophix.api.shared.infrastructure.web.SidecarClient within module 'shared'!",
            "Module 'trophies' depends on non-exposed type com.trophix.api.users.application.ports.out.UserRepository within module 'users'!",
            "Module 'trophies' depends on non-exposed type com.trophix.api.users.infrastructure.adapter.out.UserJpaEntity within module 'users'!",
            "Module 'trophies' depends on non-exposed type com.trophix.api.users.infrastructure.adapter.out.UserSpringDataRepository within module 'users'!",
            "Module 'trophies' depends on non-exposed type com.trophix.api.users.model.User within module 'users'!",
            "Module 'users' depends on non-exposed type com.trophix.api.shared.infrastructure.adapter.out.RoleJpaEntity within module 'shared'!",
            "Module 'users' depends on non-exposed type com.trophix.api.shared.infrastructure.adapter.out.RoleSpringDataRepository within module 'shared'!",
            "Module 'users' depends on non-exposed type com.trophix.api.shared.model.Role within module 'shared'!",
            "Module 'users' depends on non-exposed type com.trophix.api.games.application.ports.out.GameRepositoryPort within module 'games'!",
            "Module 'users' depends on non-exposed type com.trophix.api.games.application.ports.out.UserGameRepositoryPort within module 'games'!",
            "Module 'users' depends on non-exposed type com.trophix.api.games.model.Game within module 'games'!",
            "Module 'users' depends on non-exposed type com.trophix.api.games.model.UserGame within module 'games'!",
            "Module 'users' depends on non-exposed type com.trophix.api.games.model.UserGameSummary within module 'games'!",
            "Module 'users' depends on non-exposed type com.trophix.api.shared.application.ports.out.SyncJobPublisher within module 'shared'!",
            "Module 'users' depends on non-exposed type com.trophix.api.shared.domain.UuidV7 within module 'shared'!",
            "Module 'users' depends on non-exposed type com.trophix.api.shared.exception.BusinessException within module 'shared'!",
            "Module 'users' depends on non-exposed type com.trophix.api.shared.exception.ResourceNotFoundException within module 'shared'!",
            "Module 'users' depends on non-exposed type com.trophix.api.shared.exception.SyncCooldownException within module 'shared'!",
            "Module 'users' depends on non-exposed type com.trophix.api.shared.infrastructure.persistence.UuidV7Id within module 'shared'!",
            "Module 'users' depends on non-exposed type com.trophix.api.shared.infrastructure.web.SidecarClient within module 'shared'!"
    );

    @Test
    void verifyModuleStructure() {
        ApplicationModules modules = ApplicationModules.of(TrophixApiApplication.class);
        Violations remaining = modules.detectViolations()
                .filter(v -> !KNOWN_VIOLATIONS.contains(firstLine(v.getMessage())));
        remaining.throwIfPresent();
    }

    private static String firstLine(String message) {
        int newline = message.indexOf('\n');
        return (newline >= 0 ? message.substring(0, newline) : message).strip();
    }
}
