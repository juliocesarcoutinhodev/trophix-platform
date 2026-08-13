package com.trophix.api.shared.infrastructure.amqp;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/**
 * Declares the asynchronous sync queue topology: a durable direct exchange
 * with one work queue and a dead-letter queue for jobs that exhaust retries.
 * Messages are serialized as JSON (SyncJob).
 */
@Configuration
public class SyncQueueConfig {

    public static final String SYNC_EXCHANGE = "trophix.sync.exchange";
    public static final String SYNC_QUEUE = "trophix.sync.queue";
    public static final String SYNC_DLQ = "trophix.sync.queue.dlq";
    public static final String SYNC_ROUTING_KEY = "trophix.sync";

    @Bean
    public DirectExchange syncExchange() {
        return new DirectExchange(SYNC_EXCHANGE, true, false);
    }

    @Bean
    public Queue syncQueue() {
        return QueueBuilder.durable(SYNC_QUEUE)
                .deadLetterExchange(SYNC_EXCHANGE)
                .deadLetterRoutingKey(SYNC_DLQ)
                .build();
    }

    @Bean
    public Queue syncDlq() {
        return QueueBuilder.durable(SYNC_DLQ).build();
    }

    @Bean
    public Binding syncBinding() {
        return BindingBuilder.bind(syncQueue()).to(syncExchange()).with(SYNC_ROUTING_KEY);
    }

    @Bean
    public Binding syncDlqBinding() {
        return BindingBuilder.bind(syncDlq()).to(syncExchange()).with(SYNC_DLQ);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter(new ObjectMapper());
    }
}
