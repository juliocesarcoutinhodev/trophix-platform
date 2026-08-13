package com.trophix.api.shared.infrastructure.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient.Builder restClientBuilder(
            @Value("${trophix.sidecar.base-url}") String sidecarBaseUrl,
            @Value("${trophix.sidecar.connect-timeout}") Duration connectTimeout,
            @Value("${trophix.sidecar.read-timeout}") Duration readTimeout) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout((int) connectTimeout.toMillis());
        requestFactory.setReadTimeout((int) readTimeout.toMillis());
        return RestClient.builder()
                .baseUrl(sidecarBaseUrl)
                .requestFactory(requestFactory);
    }
}
