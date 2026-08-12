package com.trophix.api.guides.infrastructure.adapter.out;

import com.trophix.api.games.infrastructure.adapter.out.GameSpringDataRepository;
import com.trophix.api.guides.application.ports.out.GuideRepositoryPort;
import com.trophix.api.guides.model.Guide;
import com.trophix.api.guides.model.GuideStatus;
import com.trophix.api.trophies.infrastructure.adapter.out.TrophySpringDataRepository;
import com.trophix.api.users.infrastructure.adapter.out.UserSpringDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class GuideJpaAdapter implements GuideRepositoryPort {

    private final GuideSpringDataRepository springDataRepository;
    private final TrophySpringDataRepository trophySpringDataRepository;
    private final GameSpringDataRepository gameSpringDataRepository;
    private final UserSpringDataRepository userSpringDataRepository;
    private final GuideMapper mapper;

    @Override
    @Transactional
    public Guide save(Guide guide) {
        GuideEntity entity = new GuideEntity();
        entity.setId(guide.id());
        if (guide.trophyId() != null) {
            entity.setTrophy(trophySpringDataRepository.getReferenceById(guide.trophyId()));
        }
        if (guide.gameId() != null) {
            entity.setGame(gameSpringDataRepository.getReferenceById(guide.gameId()));
        }
        entity.setAuthor(userSpringDataRepository.getReferenceById(guide.authorId()));
        entity.setContent(guide.content());
        entity.setVideoUrl(guide.videoUrl());
        entity.setStatus(guide.status());
        entity.setUpvotesCount(guide.upvotesCount());
        return mapper.toDomain(springDataRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Guide> findById(UUID guideId) {
        return springDataRepository.findById(guideId).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(UUID guideId) {
        return springDataRepository.existsById(guideId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Guide> findByTrophyIdAndStatusOrderByUpvotesCountDesc(UUID trophyId, GuideStatus status) {
        return springDataRepository.findByTrophyIdAndStatusOrderByUpvotesCountDesc(trophyId, status)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Guide> findByGameIdAndStatusOrderByUpvotesCountDesc(UUID gameId, GuideStatus status) {
        return springDataRepository.findByGameIdAndStatusOrderByUpvotesCountDesc(gameId, status)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional
    public void incrementUpvotesCount(UUID guideId) {
        springDataRepository.incrementUpvotesCount(guideId);
    }

    @Override
    @Transactional
    public void decrementUpvotesCount(UUID guideId) {
        springDataRepository.decrementUpvotesCount(guideId);
    }

    @Override
    @Transactional
    public void updateStatus(UUID guideId, GuideStatus status) {
        springDataRepository.updateStatus(guideId, status);
    }
}