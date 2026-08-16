package com.trophix.api.admin.infrastructure.adapter.out;

import com.trophix.api.admin.application.ports.out.SidecarHealthPort;
import com.trophix.api.shared.infrastructure.web.SidecarClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Delegates the sidecar liveness probe to the shared {@link SidecarClient},
 * which applies the configured timeouts and circuit breaker.
 */
@Component
@RequiredArgsConstructor
public class SidecarHealthAdapter implements SidecarHealthPort {

    private final SidecarClient sidecarClient;

    @Override
    public boolean isUp() {
        return sidecarClient.ping();
    }
}
