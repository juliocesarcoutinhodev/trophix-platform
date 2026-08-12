package com.trophix.api.users.infrastructure.adapter.out;

import com.trophix.api.shared.exception.PsnServiceException;
import com.trophix.api.shared.exception.ResourceNotFoundException;
import com.trophix.api.users.application.ports.out.PsnProfileFetcherPort;
import com.trophix.api.users.application.ports.out.PsnSyncPort;
import com.trophix.api.users.model.PsnProfile;
import com.trophix.api.users.model.PsnProfileSummary;
import com.trophix.api.users.model.PsnUserGame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class SidecarPsnAdapter implements PsnProfileFetcherPort, PsnSyncPort {

    private static final int SIDECAR_NOT_FOUND = 404;

    private final RestClient restClient;

    public SidecarPsnAdapter(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.build();
    }

    @Override
    public PsnProfile fetchByPsnId(String psnId) {
        try {
            SidecarProfileResponse response = restClient.get()
                    .uri("/api/perfil/{psnId}", psnId)
                    .retrieve()
                    .onStatus(status -> status.value() == SIDECAR_NOT_FOUND,
                            (request, responseBody) -> {
                                throw new ResourceNotFoundException("Usuario nao encontrado na PSN");
                            })
                    .body(SidecarProfileResponse.class);

            return new PsnProfile(response.psnId(), response.aboutMe(), response.avatarUrl());
        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            log.error("Falha ao consultar o sidecar PSN para psnId={}", psnId, ex);
            throw new PsnServiceException("Falha ao consultar a PSN. Tente novamente mais tarde.");
        }
    }

    @Override
    public PsnProfileSummary fetchProfileSummary(String psnId) {
        try {
            SidecarSummaryResponse response = restClient.get()
                    .uri("/api/resumo/{psnId}", psnId)
                    .retrieve()
                    .onStatus(status -> status.value() == SIDECAR_NOT_FOUND,
                            (request, responseBody) -> {
                                throw new ResourceNotFoundException("Usuario nao encontrado na PSN");
                            })
                    .body(SidecarSummaryResponse.class);

            return new PsnProfileSummary(
                    response.accountId(),
                    response.nivel(),
                    response.progresso(),
                    response.trofeus().bronze(),
                    response.trofeus().prata(),
                    response.trofeus().ouro(),
                    response.trofeus().platina());
        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            log.error("Falha ao consultar o resumo PSN para psnId={}", psnId, ex);
            throw new PsnServiceException("Falha ao consultar a PSN. Tente novamente mais tarde.");
        }
    }

    @Override
    public List<PsnUserGame> fetchUserGames(String accountId) {
        try {
            SidecarUserGameResponse[] response = restClient.get()
                    .uri("/api/jogos-usuario/{accountId}", accountId)
                    .retrieve()
                    .onStatus(status -> status.value() == SIDECAR_NOT_FOUND,
                            (request, responseBody) -> {
                                throw new ResourceNotFoundException("Usuario nao encontrado na PSN");
                            })
                    .body(SidecarUserGameResponse[].class);

            if (response == null) {
                return List.of();
            }
            return Arrays.stream(response).map(this::toPsnUserGame).toList();
        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            log.error("Falha ao consultar os jogos do usuario na PSN para accountId={}", accountId, ex);
            throw new PsnServiceException("Falha ao consultar a PSN. Tente novamente mais tarde.");
        }
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