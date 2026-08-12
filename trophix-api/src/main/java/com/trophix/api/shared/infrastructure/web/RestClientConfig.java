package com.trophix.api.shared.infrastructure.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient.Builder restClientBuilder(
            @Value("${trophix.sidecar.base-url}") String sidecarBaseUrl) {
        return RestClient.builder().baseUrl(sidecarBaseUrl);
    }
}