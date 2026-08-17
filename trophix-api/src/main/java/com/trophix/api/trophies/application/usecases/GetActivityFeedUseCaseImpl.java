package com.trophix.api.trophies.application.usecases;

import com.trophix.api.games.application.ports.out.GameRepositoryPort;
import com.trophix.api.games.model.Game;
import com.trophix.api.trophies.application.ports.in.GetActivityFeedUseCase;
import com.trophix.api.trophies.application.ports.out.TrophyRepositoryPort;
import com.trophix.api.trophies.application.ports.out.UserTrophyRepositoryPort;
import com.trophix.api.trophies.model.ActivityFeedItem;
import com.trophix.api.trophies.model.Trophy;
import com.trophix.api.trophies.model.UserTrophy;
import com.trophix.api.users.application.ports.out.UserRepository;
import com.trophix.api.users.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Assembles the global activity feed by batch-fetching the trophy, the author
 * and the game for a page of recently earned trophies, avoiding N+1.
 */
@Component
@RequiredArgsConstructor
public class GetActivityFeedUseCaseImpl implements GetActivityFeedUseCase {

    private final UserTrophyRepositoryPort userTrophyRepository;
    private final TrophyRepositoryPort trophyRepository;
    private final UserRepository userRepository;
    private final GameRepositoryPort gameRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<ActivityFeedItem> getFeed(Pageable pageable) {
        Page<UserTrophy> recent = userTrophyRepository.findRecentEarned(pageable);
        List<UserTrophy> items = recent.getContent();
        if (items.isEmpty()) {
            return recent.map(item -> null);
        }

        Set<UUID> trophyIds = items.stream().map(UserTrophy::trophyId).collect(Collectors.toSet());
        Set<UUID> userIds = items.stream().map(UserTrophy::userId).collect(Collectors.toSet());

        Map<UUID, Trophy> trophies = trophyRepository.findAllByIds(trophyIds);
        Set<UUID> gameIds = trophies.values().stream().map(Trophy::gameId).collect(Collectors.toSet());
        Map<UUID, User> users = userRepository.findAllByIds(userIds);
        Map<UUID, Game> games = gameRepository.findAllByIds(gameIds);

        return recent.map(item -> build(item, trophies, users, games));
    }

    private ActivityFeedItem build(UserTrophy userTrophy,
                                   Map<UUID, Trophy> trophies,
                                   Map<UUID, User> users,
                                   Map<UUID, Game> games) {
        Trophy trophy = trophies.get(userTrophy.trophyId());
        User user = users.get(userTrophy.userId());
        Game game = trophy != null ? games.get(trophy.gameId()) : null;
        return new ActivityFeedItem(
                userTrophy.userId(),
                user != null ? user.username() : null,
                user != null ? user.avatarUrl() : null,
                userTrophy.trophyId(),
                trophy != null ? trophy.name() : null,
                trophy != null ? trophy.iconUrl() : null,
                game != null ? game.name() : null,
                userTrophy.earnedAt());
    }
}
