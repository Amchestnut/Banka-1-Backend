package com.banka1.account_service.rest_client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient clientServiceClient(
            RestClient.Builder builder,
            @Value("${services.user.url}") String baseUrl
    ) {
        return builder
                .baseUrl(baseUrl)
                .requestInterceptor(new JwtAuthInterceptor())
                .build();
    }
}
