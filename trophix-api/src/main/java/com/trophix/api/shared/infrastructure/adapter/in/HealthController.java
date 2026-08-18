package com.trophix.api.shared.infrastructure.adapter.in;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Ultra-lightweight public health check for the frontend.
 *
 * <p>Deliberately touches no database and no use case: it only answers with
 * {@code 200 OK} so clients can probe liveness without loading any infra.
 */
@RestController
@RequestMapping("/api/public/health")
class HealthController {

    @GetMapping
    public ResponseEntity<Void> health() {
        return ResponseEntity.ok().build();
    }
}
