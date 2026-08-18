package com.trophix.api.games.infrastructure.adapter.out;

import com.trophix.api.games.application.ports.out.GameImportPort;
import com.trophix.api.games.model.PsnGameDetail;
import com.trophix.api.games.model.PsnTrophy;
import com.trophix.api.shared.infrastructure.web.SidecarClient;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * PSN sidecar adapter for the games module: fetches official game details and
 * the raw trophy catalog used by the admin import flow.
 */
@Component
public class SidecarGameImportAdapter implements GameImportPort {

    private final SidecarClient sidecarClient;

    public SidecarGameImportAdapter(SidecarClient sidecarClient) {
        this.sidecarClient = sidecarClient;
    }

    @Override
    public PsnGameDetail fetchDetails(String npCommunicationId) {
        SidecarGameDetailResponse response = sidecarClient.get(
                "/api/jogos/{npCommunicationId}/details", SidecarGameDetailResponse.class,
                "Jogo não encontrado na PSN", npCommunicationId);
        return new PsnGameDetail(
                response.name(),
                response.coverUrl(),
                response.platform(),
                response.totalTrophies());
    }

    @Override
    public List<PsnTrophy> fetchTrophyCatalog(String npCommunicationId) {
        SidecarTrophyResponse[] response = sidecarClient.get(
                "/api/jogos/{npCommunicationId}/trofeus", SidecarTrophyResponse[].class,
                "Jogo não encontrado na PSN", npCommunicationId);

        if (response == null) {
            return List.of();
        }
        return Arrays.stream(response)
                .map(t -> new PsnTrophy(t.idTrofeu(), t.nome(), t.descricao(), t.tipo(), t.iconeUrl(), t.raridade()))
                .toList();
    }

    public record SidecarGameDetailResponse(
            String name,
            String coverUrl,
            String platform,
            Integer totalTrophies) {
    }

    public record SidecarTrophyResponse(
            Integer idTrofeu,
            String nome,
            String descricao,
            String tipo,
            String iconeUrl,
            Double raridade) {
    }
}
