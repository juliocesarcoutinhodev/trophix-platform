package com.trophix.api.trophies.infrastructure.adapter.out;

import com.trophix.api.trophies.application.ports.out.UserTrophyRepositoryPort;
import com.trophix.api.trophies.model.UserTrophy;
import com.trophix.api.users.infrastructure.adapter.out.UserSpringDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserTrophyJpaAdapter implements UserTrophyRepositoryPort {

    private final UserTrophySpringDataRepository springDataRepository;
    private final UserSpringDataRepository userSpringDataRepository;
    private final TrophySpringDataRepository trophySpringDataRepository;

    @Override
    @Transactional
    public void saveAll(List<UserTrophy> userTrophies) {
        for (UserTrophy userTrophy : userTrophies) {
            UserTrophyEntity entity = springDataRepository
                    .findByUserIdAndTrophyId(userTrophy.userId(), userTrophy.trophyId())
                    .orElseGet(() -> {
                        UserTrophyEntity created = new UserTrophyEntity();
                        created.setUser(userSpringDataRepository.getReferenceById(userTrophy.userId()));
                        created.setTrophy(trophySpringDataRepository.getReferenceById(userTrophy.trophyId()));
                        return created;
                    });

            entity.setEarnedAt(userTrophy.earnedAt());
            springDataRepository.save(entity);
        }
    }
}