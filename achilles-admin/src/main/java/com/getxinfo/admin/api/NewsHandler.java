package com.getxinfo.admin.api;

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
public class NewsHandler {

    private final WebClient webClient;
    @Value("${content-api.url}")
    private String contentApiUrl;

    public NewsHandler(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public Mono<ServerResponse> getAllNews(ServerRequest request) {
        return webClient.get().uri(contentApiUrl + "/news").accept(MediaType.APPLICATION_JSON)
                .exchange().flatMap(clientResponse -> ServerResponse.status(clientResponse.statusCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(clientResponse.bodyToFlux(DataBuffer.class), DataBuffer.class));
    }

    public Mono<ServerResponse> getNews(ServerRequest request) {
        return webClient.get().uri(contentApiUrl + "/news/" + request.pathVariable("newsId"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange().flatMap(clientResponse -> ServerResponse.status(clientResponse.statusCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(clientResponse.bodyToFlux(DataBuffer.class), DataBuffer.class));
    }

    public Mono<ServerResponse> createNews(ServerRequest request) {
        return webClient.post().uri(contentApiUrl + "/news")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request.bodyToFlux(DataBuffer.class), DataBuffer.class)
                .accept(MediaType.APPLICATION_JSON)
                .exchange().flatMap(clientResponse -> ServerResponse.status(clientResponse.statusCode())
                        .headers(headers -> clientResponse.headers())
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(clientResponse.bodyToFlux(DataBuffer.class), DataBuffer.class));
    }

    public Mono<ServerResponse> updateNews(ServerRequest request) {
        return webClient.put().uri(contentApiUrl + "/news/" + request.pathVariable("newsId"))
                .contentType(MediaType.APPLICATION_JSON)
                .body(request.bodyToFlux(DataBuffer.class), DataBuffer.class)
                .accept(MediaType.APPLICATION_JSON)
                .exchange().flatMap(clientResponse -> ServerResponse.status(clientResponse.statusCode())
                        .headers(headers -> clientResponse.headers())
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(clientResponse.bodyToFlux(DataBuffer.class), DataBuffer.class));
    }

}
