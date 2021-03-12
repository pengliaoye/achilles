package com.getxinfo.app.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@Validated
public class UserHandler {

    private final WebClient webClient;
    @Value("${supoort-api.url}")
    private String supportApiUrl;

    public UserHandler(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public Mono<ServerResponse> sendSmsCode(ServerRequest request) {
        String telephone = request.queryParam("telephone").get();
        return webClient.get().uri(supportApiUrl + "/users/smscode?swissNumberStr=" + telephone)
                .exchange().flatMap(clientResponse -> ServerResponse.status(clientResponse.statusCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(clientResponse.bodyToFlux(DataBuffer.class), DataBuffer.class));
    }

    public Mono<ServerResponse> authentication(ServerRequest request) {
        String telephone = request.queryParam("telephone").get();
        String code = request.queryParam("code").get();
        return webClient.get().uri(supportApiUrl + "/users/auth?telephone=" + telephone + "&code=" + code)
                .exchange().flatMap(clientResponse -> ServerResponse.status(clientResponse.statusCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(clientResponse.bodyToFlux(DataBuffer.class), DataBuffer.class));
    }

}
