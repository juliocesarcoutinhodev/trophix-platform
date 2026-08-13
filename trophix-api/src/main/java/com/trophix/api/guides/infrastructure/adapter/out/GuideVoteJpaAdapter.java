package com.trophix.api.guides.infrastructure.adapter.out;

import com.trophix.api.guides.application.ports.out.GuideVoteRepositoryPort;
import com.trophix.api.guides.model.GuideVote;
import com.trophix.api.users.infrastructure.adapter.out.UserSpringDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class GuideVoteJpaAdapter implements GuideVoteRepositoryPort {

    private final GuideVoteSpringDataRepository springDataRepository;
    private final GuideSpringDataRepository guideSpringDataRepository;
    private final UserSpringDataRepository userSpringDataRepository;

    @Override
    @Transactional(readOnly = true)
    public boolean existsByGuideIdAndUserId(UUID guideId, UUID userId) {
        return springDataRepository.existsByGuideIdAndUserId(guideId, userId);
    }

    @Override
    @Transactional
    public void save(GuideVote vote) {
        GuideVoteEntity entity = new GuideVoteEntity();
        entity.setGuide(guideSpringDataRepository.getReferenceById(vote.guideId()));
        entity.setUser(userSpringDataRepository.getReferenceById(vote.userId()));
        entity.setVotedAt(vote.votedAt());
        springDataRepository.save(entity);
    }

    @Override
    @Transactional
    public void deleteByGuideIdAndUserId(UUID guideId, UUID userId) {
        springDataRepository.deleteByGuideIdAndUserId(guideId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<UUID> findVotedGuideIdsByUser(UUID userId, Collection<UUID> guideIds) {
        if (guideIds.isEmpty()) {
            return Set.of();
        }
        return springDataRepository.findVotedGuideIdsByUser(userId, guideIds);
    }
}