package com.trophix.api.games.infrastructure.adapter.in;

import com.trophix.api.games.application.ports.in.GetGameCatalogUseCase;
import com.trophix.api.games.infrastructure.adapter.in.dto.GameCatalogDTO;
import com.trophix.api.games.infrastructure.adapter.in.mapper.GameWebMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Public game catalog over the internal database, ordered by popularity
 * (number of owners). The database is populated passively via user syncs.
 */
@RestController
@RequestMapping("/api/public/games")
@RequiredArgsConstructor
public class GameCatalogController {

    private static final int DEFAULT_PAGE_SIZE = 20;

    private final GetGameCatalogUseCase getGameCatalogUseCase;
    private final GameWebMapper gameWebMapper;

    @GetMapping
    public ResponseEntity<Page<GameCatalogDTO>> getCatalog(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<GameCatalogDTO> catalog = getGameCatalogUseCase
                .getCatalog(search, PageRequest.of(Math.max(page, 0), Math.min(size, 100)))
                .map(gameWebMapper::toGameCatalogDTO);
        return ResponseEntity.ok(catalog);
    }

    @GetMapping("/trending")
    public ResponseEntity<List<GameCatalogDTO>> getTrending(@RequestParam(defaultValue = "10") int limit) {
        List<GameCatalogDTO> trending = getGameCatalogUseCase.getTrending(limit).stream()
                .map(gameWebMapper::toGameCatalogDTO)
                .toList();
        return ResponseEntity.ok(trending);
    }
}
