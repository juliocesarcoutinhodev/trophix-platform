package com.trophix.api.shared.infrastructure.async;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Enables scheduling for the daily PSN sync. Asynchronous processing now
 * runs through the RabbitMQ sync queue consumer instead of an in-memory pool.
 */
@Configuration
@EnableScheduling
public class AsyncConfig {
}
