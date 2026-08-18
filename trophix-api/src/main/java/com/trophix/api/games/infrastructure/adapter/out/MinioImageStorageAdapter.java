package com.trophix.api.games.infrastructure.adapter.out;

import com.trophix.api.games.application.ports.out.ImageStoragePort;
import com.trophix.api.shared.exception.StorageException;
import com.trophix.api.shared.infrastructure.storage.MinioStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Downloads a remote image and stores it in MinIO, returning the new public
 * URL. Runs on virtual threads (default executor), so the blocking HTTP call
 * is cheap.
 */
@Slf4j
@Component
public class MinioImageStorageAdapter implements ImageStoragePort {

    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(5);
    private static final Duration READ_TIMEOUT = Duration.ofSeconds(10);

    private final MinioStorageService storage;
    private final HttpClient httpClient;

    public MinioImageStorageAdapter(MinioStorageService storage) {
        this.storage = storage;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(CONNECT_TIMEOUT)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    @Override
    public String downloadAndStore(String sourceUrl, String folder, String filename) {
        HttpResponse<byte[]> response;
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(sourceUrl))
                    .timeout(READ_TIMEOUT)
                    .GET()
                    .build();
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
        } catch (Exception ex) {
            log.error("Falha ao baixar imagem de '{}'", sourceUrl, ex);
            throw new StorageException("Falha ao baixar a imagem remota.");
        }

        if (response.statusCode() >= 400) {
            log.error("Download da imagem falhou com status {}: {}", response.statusCode(), sourceUrl);
            throw new StorageException("Falha ao baixar a imagem remota (status " + response.statusCode() + ").");
        }

        byte[] body = response.body();
        String contentType = response.headers().firstValue("content-type").orElse("image/png");
        try (InputStream stream = new ByteArrayInputStream(body)) {
            return storage.upload(stream, body.length, contentType, folder, filename);
        } catch (StorageException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Falha ao armazenar imagem '{}' em '{}'", sourceUrl, folder, ex);
            throw new StorageException("Falha ao armazenar a imagem no armazenamento.");
        }
    }
}
