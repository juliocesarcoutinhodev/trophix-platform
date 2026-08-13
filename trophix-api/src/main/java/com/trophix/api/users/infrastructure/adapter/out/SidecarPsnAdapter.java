package com.trophix.api.users.infrastructure.adapter.out;

import com.trophix.api.shared.infrastructure.web.SidecarClient;
import com.trophix.api.users.application.ports.out.PsnProfileFetcherPort;
import com.trophix.api.users.application.ports.out.PsnSyncPort;
import com.trophix.api.users.model.PsnProfile;
import com.trophix.api.users.model.PsnProfileSummary;
import com.trophix.api.users.model.PsnUserGame;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@Component
public class SidecarPsnAdapter implements PsnProfileFetcherPort, PsnSyncPort {

    private final SidecarClient sidecarClient;

    public SidecarPsnAdapter(SidecarClient sidecarClient) {
        this.sidecarClient = sidecarClient;
    }

    @Override
    public PsnProfile fetchByPsnId(String psnId) {
        SidecarProfileResponse response = sidecarClient.get(
                "/api/perfil/{psnId}", SidecarProfileResponse.class,
                "Usuario nao encontrado na PSN", psnId);
        return new PsnProfile(response.psnId(), response.aboutMe(), response.avatarUrl());
    }

    @Override
    public PsnProfileSummary fetchProfileSummary(String psnId) {
        SidecarSummaryResponse response = sidecarClient.get(
                "/api/resumo/{psnId}", SidecarSummaryResponse.class,
                "Usuario nao encontrado na PSN", psnId);
        return new PsnProfileSummary(
                response.accountId(),
                response.nivel(),
                response.progresso(),
                response.trofeus().bronze(),
                response.trofeus().prata(),
                response.trofeus().ouro(),
                response.trofeus().platina());
    }

    @Override
    public List<PsnUserGame> fetchUserGames(String accountId) {
        SidecarUserGameResponse[] response = sidecarClient.get(
                "/api/jogos-usuario/{accountId}", SidecarUserGameResponse[].class,
                "Usuario nao encontrado na PSN", accountId);
        if (response == null) {
            return List.of();
        }
        return Arrays.stream(response).map(this::toPsnUserGame).toList();
    }

    private PsnUserGame toPsnUserGame(SidecarUserGameResponse response) {
        Instant lastPlayedAt = response.ultimaJogadaEm() != null
                ? Instant.parse(response.ultimaJogadaEm())
                : Instant.now();
        String platform = response.plataforma() != null
                ? String.join(",", response.plataforma())
                : null;

        return new PsnUserGame(
                response.npCommunicationId(),
                response.nome(),
                response.imagemUrl(),
                platform,
                response.progresso(),
                response.trofeusConquistados(),
                response.trofeusTotais(),
                lastPlayedAt);
    }

    public record SidecarProfileResponse(String psnId, String aboutMe, String avatarUrl) {
    }

    public record SidecarSummaryResponse(
            String accountId,
            Integer nivel,
            Integer progresso,
            SidecarTrophyCountsResponse trofeus) {
    }

    public record SidecarTrophyCountsResponse(Integer bronze, Integer prata, Integer ouro, Integer platina) {
    }

    public record SidecarUserGameResponse(
            String npCommunicationId,
            String nome,
            String imagemUrl,
            List<String> plataforma,
            Integer progresso,
            Integer trofeusConquistados,
            Integer trofeusTotais,
            String ultimaJogadaEm) {
    }
}
