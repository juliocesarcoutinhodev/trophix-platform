package com.trophix.api.guides.infrastructure.adapter.out;

import com.trophix.api.guides.application.ports.out.GuideRepositoryPort;
import com.trophix.api.guides.model.Guide;
import com.trophix.api.guides.model.GuideStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class GuideJpaAdapter implements GuideRepositoryPort {

    private final GuideSpringDataRepository springDataRepository;
    private final GuideMapper mapper;

    @Override
    @Transactional
    public Guide save(Guide guide) {
        GuideEntity entity = new GuideEntity();
        entity.setId(guide.id());
        entity.setTrophyId(guide.trophyId());
        entity.setGameId(guide.gameId());
        entity.setAuthorId(guide.authorId());
        entity.setTitle(guide.title());
        entity.setDescription(guide.description());
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
    public Page<Guide> findByStatus(GuideStatus status, Pageable pageable) {
        return springDataRepository.findByStatus(status, pageable).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Guide> findAll(GuideStatus status, String search, Boolean isTrophyGuide, Pageable pageable) {
        return springDataRepository.findAllFiltered(status, search, isTrophyGuide, pageable).map(mapper::toDomain);
    }

    @Override
    @Transactional
    public void delete(Guide guide) {
        springDataRepository.deleteById(guide.id());
    }

    @Override
    @Transactional(readOnly = true)
    public long countByStatus(GuideStatus status) {
        return springDataRepository.countByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByStatusSince(GuideStatus status, Instant since) {
        return springDataRepository.countByStatusSince(status, since);
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
    @Transactional(readOnly = true)
    public List<Guide> findLatestRoadmapsByStatus(GuideStatus status, String search, int limit) {
        return springDataRepository.findLatestRoadmaps(status, search, PageRequest.of(0, Math.max(1, limit)))
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Guide> findTrophyTipsByAuthorAndGame(UUID gameId, UUID authorId, GuideStatus status) {
        return springDataRepository.findTrophyTipsByAuthorAndGame(gameId, authorId, status)
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