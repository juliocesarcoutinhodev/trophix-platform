package com.trophix.api.sync.infrastructure.amqp;

import com.trophix.api.shared.application.ports.out.SyncJobPublisher;
import com.trophix.api.sync.domain.SyncJob;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RabbitSyncJobPublisher implements SyncJobPublisher {

    private final RabbitTemplate rabbitTemplate;

    public RabbitSyncJobPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publishProfileSync(UUID userId) {
        rabbitTemplate.convertAndSend(
                SyncQueueConfig.SYNC_EXCHANGE, SyncQueueConfig.SYNC_ROUTING_KEY,
                SyncJob.profileSync(userId));
    }

    @Override
    public void publishTrophySync(UUID userId, UUID gameId) {
        rabbitTemplate.convertAndSend(
                SyncQueueConfig.SYNC_EXCHANGE, SyncQueueConfig.SYNC_ROUTING_KEY,
                SyncJob.trophySync(userId, gameId));
    }

    @Override
    public void publishTrophyCatalogSync(UUID gameId) {
        rabbitTemplate.convertAndSend(
                SyncQueueConfig.SYNC_EXCHANGE, SyncQueueConfig.SYNC_ROUTING_KEY,
                SyncJob.trophyCatalogSync(gameId));
    }
}
