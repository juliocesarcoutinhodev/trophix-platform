package com.trophix.api.shared.infrastructure.web;

import com.trophix.api.shared.exception.CircuitOpenException;
import com.trophix.api.shared.exception.PsnServiceException;
import com.trophix.api.shared.exception.ResourceNotFoundException;
import com.trophix.api.shared.infrastructure.resilience.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Centralized gateway to the PSN sidecar: combines the configured RestClient
 * (with connect/read timeouts) with a {@link CircuitBreaker} so that a
 * hanging or unavailable sidecar fails fast instead of blocking threads.
 * <p>
 * Error mapping: HTTP 404 becomes a {@link ResourceNotFoundException} (a
 * legitimate "not found", not a circuit failure); any other runtime failure
 * becomes a {@link PsnServiceException} (502). While the breaker is OPEN, a
 * {@link CircuitOpenException} (503) is thrown without reaching the network.
 */
@Slf4j
@Component
public class SidecarClient {

    private static final int SIDECAR_NOT_FOUND = 404;

    private final RestClient restClient;
    private final CircuitBreaker circuitBreaker;

    public SidecarClient(RestClient.Builder restClientBuilder, CircuitBreaker circuitBreaker) {
        this.restClient = restClientBuilder.build();
        this.circuitBreaker = circuitBreaker;
    }

    public <T> T get(String uri, Class<T> responseType, String notFoundMessage, Object... uriVariables) {
        return circuitBreaker.execute(
                () -> fetch(uri, responseType, notFoundMessage, uriVariables),
                SidecarClient::countsAsCircuitFailure);
    }

    private <T> T fetch(String uri, Class<T> responseType, String notFoundMessage, Object[] uriVariables) {
        try {
            return restClient.get()
                    .uri(uri, uriVariables)
                    .retrieve()
                    .onStatus(status -> status.value() == SIDECAR_NOT_FOUND,
                            (request, response) -> {
                                throw new ResourceNotFoundException(notFoundMessage);
                            })
                    .body(responseType);
        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            log.error("Falha ao consultar o sidecar ({}): {}", uri, ex.getMessage());
            throw new PsnServiceException("Falha ao consultar a PSN. Tente novamente mais tarde.");
        }
    }

    private static boolean countsAsCircuitFailure(Throwable ex) {
        return !(ex instanceof ResourceNotFoundException);
    }
}
