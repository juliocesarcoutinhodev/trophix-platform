package com.trophix.api.trophies.infrastructure.adapter.out;

import com.trophix.api.shared.infrastructure.web.SidecarClient;
import com.trophix.api.trophies.application.ports.out.TrophySyncPort;
import com.trophix.api.trophies.model.PsnEarnedTrophy;
import com.trophix.api.trophies.model.PsnTrophy;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

/**
 * PSN sidecar adapter for the trophies module. Lives here (instead of the
 * users module) to avoid a users &harr; trophies dependency cycle.
 */
@Component
public class SidecarTrophyAdapter implements TrophySyncPort {

    private final SidecarClient sidecarClient;

    public SidecarTrophyAdapter(SidecarClient sidecarClient) {
        this.sidecarClient = sidecarClient;
    }

    @Override
    public List<PsnTrophy> fetchGameTrophyCatalog(String npCommunicationId) {
        SidecarTrophyResponse[] response = sidecarClient.get(
                "/api/jogos/{npCommunicationId}/trofeus", SidecarTrophyResponse[].class,
                "Jogo não encontrado na PSN", npCommunicationId);

        if (response == null) {
            return List.of();
        }
        return Arrays.stream(response)
                .map(t -> new PsnTrophy(t.idTrofeu(), t.nome(), t.descricao(), t.tipo(), t.iconeUrl()))
                .toList();
    }

    @Override
    public List<PsnEarnedTrophy> fetchUserEarnedTrophies(String accountId, String npCommunicationId) {
        SidecarEarnedTrophyResponse[] response = sidecarClient.get(
                "/api/jogos/{npCommunicationId}/trofeus-conquistados/{accountId}",
                SidecarEarnedTrophyResponse[].class,
                "Jogo ou usuário não encontrado na PSN",
                npCommunicationId, accountId);

        if (response == null) {
            return List.of();
        }
        return Arrays.stream(response)
                .map(t -> new PsnEarnedTrophy(
                        t.idTrofeu(),
                        Boolean.TRUE.equals(t.conquistado()),
                        t.conquistadoEm() != null ? Instant.parse(t.conquistadoEm()) : null))
                .toList();
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
