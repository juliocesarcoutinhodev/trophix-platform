package com.trophix.api.ai.infrastructure.amqp;

import com.trophix.api.ai.application.ports.in.GenerateGuideAiUseCase;
import com.trophix.api.ai.domain.GuideAiJob;
import com.trophix.api.shared.exception.BusinessException;
import com.trophix.api.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consumes AI generation jobs from the queue. Permanent errors (missing
 * guide/game/trophy) are logged and skipped without retry. LLM failures and
 * empty outputs are handled inside the use case by writing a failure message
 * into the guide content, so the frontend polling unblocks.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GuideAiJobConsumer {

    private final GenerateGuideAiUseCase generateGuideAiUseCase;

    @RabbitListener(queues = GuideAiQueueConfig.GUIDE_AI_QUEUE)
    public void onMessage(GuideAiJob job) {
        try {
            generateGuideAiUseCase.generate(job);
        } catch (BusinessException | ResourceNotFoundException ex) {
            log.warn("Job de IA ignorado (erro permanente): type={} guideId={} trophyId={} -> {}",
                    job.type(), job.guideId(), job.trophyId(), ex.getMessage());
        }
    }
}
