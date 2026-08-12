package com.trophix.api.trophies.infrastructure.adapter.out;

import com.trophix.api.shared.exception.PsnServiceException;
import com.trophix.api.shared.exception.ResourceNotFoundException;
import com.trophix.api.trophies.application.ports.out.TrophySyncPort;
import com.trophix.api.trophies.model.PsnEarnedTrophy;
import com.trophix.api.trophies.model.PsnTrophy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

/**
 * PSN sidecar adapter for the trophies module. Lives here (instead of the
 * users module) to avoid a users &harr; trophies dependency cycle.
 */
@Component
@Slf4j
public class SidecarTrophyAdapter implements TrophySyncPort {

    private static final int SIDECAR_NOT_FOUND = 404;

    private final RestClient restClient;

    public SidecarTrophyAdapter(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.build();
    }

    @Override
    public List<PsnTrophy> fetchGameTrophyCatalog(String npCommunicationId) {
        try {
            SidecarTrophyResponse[] response = restClient.get()
                    .uri("/api/jogos/{npCommunicationId}/trofeus", npCommunicationId)
                    .retrieve()
                    .onStatus(status -> status.value() == SIDECAR_NOT_FOUND,
                            (request, responseBody) -> {
                                throw new ResourceNotFoundException("Jogo não encontrado na PSN");
                            })
                    .body(SidecarTrophyResponse[].class);

            if (response == null) {
                return List.of();
            }
            return Arrays.stream(response)
                    .map(t -> new PsnTrophy(t.idTrofeu(), t.nome(), t.descricao(), t.tipo(), t.iconeUrl()))
                    .toList();
        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            log.error("Falha ao consultar o catalogo de trofeus para npCommunicationId={}", npCommunicationId, ex);
            throw new PsnServiceException("Falha ao consultar a PSN. Tente novamente mais tarde.");
        }
    }

    @Override
    public List<PsnEarnedTrophy> fetchUserEarnedTrophies(String accountId, String npCommunicationId) {
        try {
            SidecarEarnedTrophyResponse[] response = restClient.get()
                    .uri("/api/jogos/{npCommunicationId}/trofeus-conquistados/{accountId}",
                            npCommunicationId, accountId)
                    .retrieve()
                    .onStatus(status -> status.value() == SIDECAR_NOT_FOUND,
                            (request, responseBody) -> {
                                throw new ResourceNotFoundException("Jogo ou usuário não encontrado na PSN");
                            })
                    .body(SidecarEarnedTrophyResponse[].class);

            if (response == null) {
                return List.of();
            }
            return Arrays.stream(response)
                    .map(t -> new PsnEarnedTrophy(
                            t.idTrofeu(),
                            Boolean.TRUE.equals(t.conquistado()),
                            t.conquistadoEm() != null ? Instant.parse(t.conquistadoEm()) : null))
                    .toList();
        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            log.error("Falha ao consultar trofeus conquistados para accountId={} npCommunicationId={}",
                    accountId, npCommunicationId, ex);
            throw new PsnServiceException("Falha ao consultar a PSN. Tente novamente mais tarde.");
        }
    }

    public record SidecarTrophyResponse(
            Integer idTrofeu,
            String nome,
            String descricao,
            String tipo,
            String iconeUrl) {
    }

    public record SidecarEarnedTrophyResponse(
            Integer idTrofeu,
            Boolean conquistado,
            String conquistadoEm) {
    }
}