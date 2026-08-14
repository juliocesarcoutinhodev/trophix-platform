package com.trophix.api.shared.infrastructure.async;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Enables scheduling (daily PSN sync, token cleanups) and asynchronous
 * execution (non-blocking e-mail delivery).
 */
@Configuration
@EnableAsync
@EnableScheduling
public class AsyncConfig {
}
