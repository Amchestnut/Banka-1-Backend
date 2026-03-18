package com.banka1.account_service.rest_client;

import com.banka1.account_service.dto.response.ClientIdResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class ClientServiceClient {

    private final RestClient clientServiceClient;

    public ClientIdResponseDto getUser(String jmbg) {
        return clientServiceClient.get()
                .uri("/customers/jmbg/{jmbg}", jmbg)
                .retrieve()
                .body(ClientIdResponseDto.class);
    }
}
