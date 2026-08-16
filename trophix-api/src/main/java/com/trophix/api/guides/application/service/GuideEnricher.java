package com.trophix.api.guides.application.service;

import com.trophix.api.games.application.ports.out.GameRepositoryPort;
import com.trophix.api.games.model.Game;
import com.trophix.api.guides.application.ports.out.GuideVoteRepositoryPort;
import com.trophix.api.guides.model.Guide;
import com.trophix.api.guides.model.GuideListItem;
import com.trophix.api.users.application.ports.out.UserRepository;
import com.trophix.api.users.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Assembles {@link GuideListItem} read models for a viewer by batch-fetching
 * the target game (name/image), the author (name) and the viewer's votes with
 * IN queries, avoiding N+1. For anonymous viewers (null user) the vote check
 * is skipped and {@code currentUserVoted} is always false.
 */
@Component
@RequiredArgsConstructor
public class GuideEnricher {

    private final GameRepositoryPort gameRepository;
    private final UserRepository userRepository;
    private final GuideVoteRepositoryPort voteRepository;

    public GuideListItem enrich(Guide guide, UUID currentUserId) {
        Map<UUID, Game> gamesById = gameRepository.findAllByIds(ids(Stream.of(guide.gameId())));
        Map<UUID, User> authorsById = userRepository.findAllByIds(ids(Stream.of(guide.authorId())));
        Set<UUID> votedGuideIds = votedGuideIds(currentUserId, Set.of(guide.id()));
        return build(guide, gamesById, authorsById, votedGuideIds);
    }

    public List<GuideListItem> enrichAll(List<Guide> guides, UUID currentUserId) {
        Map<UUID, Game> gamesById = gameRepository.findAllByIds(ids(guides.stream().map(Guide::gameId)));
        Map<UUID, User> authorsById = userRepository.findAllByIds(ids(guides.stream().map(Guide::authorId)));
        Set<UUID> votedGuideIds = votedGuideIds(currentUserId, ids(guides.stream().map(Guide::id)));
        return guides.stream()
                .map(guide -> build(guide, gamesById, authorsById, votedGuideIds))
                .toList();
    }

    private GuideListItem build(Guide guide, Map<UUID, Game> gamesById,
                                Map<UUID, User> authorsById, Set<UUID> votedGuideIds) {
        return new GuideListItem(
                guide,
                nameOf(gamesById.get(guide.gameId())),
                imageOf(gamesById.get(guide.gameId())),
                usernameOf(authorsById.get(guide.authorId())),
                avatarOf(authorsById.get(guide.authorId())),
                votedGuideIds.contains(guide.id()));
    }

    private Set<UUID> votedGuideIds(UUID currentUserId, Set<UUID> guideIds) {
        if (currentUserId == null || guideIds.isEmpty()) {
            return Set.of();
        }
        return voteRepository.findVotedGuideIdsByUser(currentUserId, guideIds);
    }

    private Set<UUID> ids(Stream<UUID> stream) {
        return stream.filter(Objects::nonNull).collect(Collectors.toSet());
    }

    private String nameOf(Game game) {
        return game != null ? game.name() : null;
    }

    private String imageOf(Game game) {
        return game != null ? game.imageUrl() : null;
    }

    private String usernameOf(User user) {
        return user != null ? user.username() : null;
    }

    private String avatarOf(User user) {
        return user != null ? user.avatarUrl() : null;
    }
}
