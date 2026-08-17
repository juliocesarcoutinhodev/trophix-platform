package com.trophix.api.trophies.application.usecases;

import com.trophix.api.games.application.ports.out.GameRepositoryPort;
import com.trophix.api.games.model.Game;
import com.trophix.api.shared.exception.BusinessException;
import com.trophix.api.shared.exception.ResourceNotFoundException;
import com.trophix.api.trophies.application.ports.in.SyncGameTrophiesUseCase;
import com.trophix.api.trophies.application.ports.out.TrophyRepositoryPort;
import com.trophix.api.trophies.application.ports.out.TrophySyncPort;
import com.trophix.api.trophies.application.ports.out.UserTrophyRepositoryPort;
import com.trophix.api.trophies.model.PsnEarnedTrophy;
import com.trophix.api.trophies.model.PsnTrophy;
import com.trophix.api.trophies.model.Trophy;
import com.trophix.api.trophies.model.UserTrophy;
import com.trophix.api.users.application.ports.out.UserRepository;
import com.trophix.api.users.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class SyncGameTrophiesUseCaseImpl implements SyncGameTrophiesUseCase {

    private final UserRepository userRepository;
    private final GameRepositoryPort gameRepository;
    private final TrophySyncPort trophySync;
    private final TrophyRepositoryPort trophyRepository;
    private final UserTrophyRepositoryPort userTrophyRepository;

    @Override
    @Transactional
    public String sync(UUID userId, UUID gameId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        String accountId = user.accountId();
        if (accountId == null) {
            throw new BusinessException("Sincronize o perfil do usuário antes de sincronizar os troféus.");
        }

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Jogo não encontrado"));

        List<Trophy> trophies = fetchAndSaveCatalog(gameId, game.npCommunicationId());

        Map<Integer, UUID> trophyIdByPsnId = trophies.stream()
                .collect(Collectors.toMap(Trophy::psnTrophyId, Trophy::id));

        List<PsnEarnedTrophy> earnedStatus = trophySync.fetchUserEarnedTrophies(accountId, game.npCommunicationId());

        Map<Integer, Double> rarityByPsnId = earnedStatus.stream()
                .filter(status -> status.rarity() != null && trophyIdByPsnId.containsKey(status.psnTrophyId()))
                .collect(Collectors.toMap(PsnEarnedTrophy::psnTrophyId, PsnEarnedTrophy::rarity, (a, b) -> a));
        if (!rarityByPsnId.isEmpty()) {
            trophyRepository.updateRarity(gameId, rarityByPsnId);
        }

        List<UserTrophy> userTrophies = earnedStatus.stream()
                .filter(PsnEarnedTrophy::earned)
                .filter(earned -> trophyIdByPsnId.containsKey(earned.psnTrophyId()))
                .map(earned -> UserTrophy.create(userId, trophyIdByPsnId.get(earned.psnTrophyId()), earned.earnedAt()))
                .toList();

        userTrophyRepository.saveAll(userTrophies);

        log.info("Troféus sincronizados para userId={} gameId={} catalogo={} conquistados={}",
                userId, gameId, trophies.size(), userTrophies.size());
        return "Troféus sincronizados com sucesso!";
    }

    @Override
    @Transactional
    public String syncCatalog(UUID gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Jogo não encontrado"));

        int catalogSize = fetchAndSaveCatalog(gameId, game.npCommunicationId()).size();
        log.info("Catálogo de troféus sincronizado para gameId={} trofeus={}", gameId, catalogSize);
        return "Catálogo de troféus sincronizado com sucesso!";
    }

    private List<Trophy> fetchAndSaveCatalog(UUID gameId, String npCommunicationId) {
        List<PsnTrophy> catalog = trophySync.fetchGameTrophyCatalog(npCommunicationId);
        return trophyRepository.saveAllIfNotExists(gameId, catalog.stream()
                .map(p -> Trophy.create(gameId, p.psnTrophyId(), p.name(), p.description(),
                        p.type(), p.iconUrl(), p.rarity()))
                .toList());
    }
}