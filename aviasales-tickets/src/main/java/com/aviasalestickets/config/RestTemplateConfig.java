package com.aviasalestickets.config;

import com.aviasalestickets.config.properties.TripRestTemplateProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class RestTemplateConfig {

    private final TripRestTemplateProperties tripRestTemplateProperties;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        log.info("Rest template trip service uri: {}", tripRestTemplateProperties.getTripService());
        return builder
                .rootUri(tripRestTemplateProperties.getTripService())
                .build();
    }

}
