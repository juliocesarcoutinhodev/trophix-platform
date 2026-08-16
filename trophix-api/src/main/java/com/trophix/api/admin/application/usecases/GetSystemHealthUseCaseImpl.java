package com.trophix.api.admin.application.usecases;

import com.trophix.api.admin.application.ports.in.GetSystemHealthUseCase;
import org.springframework.stereotype.Component;

/**
 * The API is by definition up when this use case runs: no database or
 * external calls, so it stays trivially fast for latency probes.
 */
@Component
public class GetSystemHealthUseCaseImpl implements GetSystemHealthUseCase {

    @Override
    public SystemHealth getHealth() {
        return new SystemHealth(true);
    }
}
