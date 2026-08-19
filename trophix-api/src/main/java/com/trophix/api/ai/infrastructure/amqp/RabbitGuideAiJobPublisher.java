package com.trophix.api.ai.infrastructure.amqp;

import com.trophix.api.ai.application.ports.out.GuideAiJobPublisher;
import com.trophix.api.ai.domain.GuideAiJob;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RabbitGuideAiJobPublisher implements GuideAiJobPublisher {

    private final RabbitTemplate rabbitTemplate;

    public RabbitGuideAiJobPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publishRoadmapGeneration(UUID guideId) {
        rabbitTemplate.convertAndSend(
                GuideAiQueueConfig.GUIDE_AI_EXCHANGE, GuideAiQueueConfig.GUIDE_AI_ROUTING_KEY,
                GuideAiJob.roadmap(guideId));
    }

    @Override
    public void publishTrophyTipGeneration(UUID guideId, UUID trophyId) {
        rabbitTemplate.convertAndSend(
                GuideAiQueueConfig.GUIDE_AI_EXCHANGE, GuideAiQueueConfig.GUIDE_AI_ROUTING_KEY,
                GuideAiJob.trophyTip(guideId, trophyId));
    }
}
