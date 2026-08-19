package com.trophix.api.ai.infrastructure.amqp;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Declares the asynchronous AI guide generation queue topology: a durable
 * direct exchange with one work queue and a dead-letter queue for jobs that
 * exhaust retries. Reuses the shared {@code Jackson2JsonMessageConverter}
 * bean declared by the sync module.
 */
@Configuration
public class GuideAiQueueConfig {

    public static final String GUIDE_AI_EXCHANGE = "trophix.ai.exchange";
    public static final String GUIDE_AI_QUEUE = "trophix.ai.queue";
    public static final String GUIDE_AI_DLQ = "trophix.ai.queue.dlq";
    public static final String GUIDE_AI_ROUTING_KEY = "trophix.ai";

    @Bean
    public DirectExchange guideAiExchange() {
        return new DirectExchange(GUIDE_AI_EXCHANGE, true, false);
    }

    @Bean
    public Queue guideAiQueue() {
        return QueueBuilder.durable(GUIDE_AI_QUEUE)
                .deadLetterExchange(GUIDE_AI_EXCHANGE)
                .deadLetterRoutingKey(GUIDE_AI_DLQ)
                .build();
    }

    @Bean
    public Queue guideAiDlq() {
        return QueueBuilder.durable(GUIDE_AI_DLQ).build();
    }

    @Bean
    public Binding guideAiBinding() {
        return BindingBuilder.bind(guideAiQueue()).to(guideAiExchange()).with(GUIDE_AI_ROUTING_KEY);
    }

    @Bean
    public Binding guideAiDlqBinding() {
        return BindingBuilder.bind(guideAiDlq()).to(guideAiExchange()).with(GUIDE_AI_DLQ);
    }
}
