package com.getxinfo.content.api;

import com.getxinfo.content.business.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

import static org.springframework.web.reactive.function.server.ServerResponse.notFound;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
@Validated
public class NewsHandler {

    private final NewsService newsService;
    private final NewsResourceAssembler newsResourceAssembler;

    @Autowired
    public NewsHandler(NewsService newsService, NewsResourceAssembler newsResourceAssembler) {
        this.newsService = newsService;
        this.newsResourceAssembler = newsResourceAssembler;
    }

    public Mono<ServerResponse> getAllNews(ServerRequest request) {
//        PageRequest.of(request.queryParam("page"), request.queryParam("size"));
        return ok().contentType(MediaType.APPLICATION_JSON)
                .body(newsService.findAll(), Map.class);
    }

    public Mono<ServerResponse> getNews(ServerRequest request) {
        return newsService
                .findById(Long.valueOf(request.pathVariable("newsId")))
                .map(newsResourceAssembler::toResource)
                .flatMap(
                        news ->
                                ok().contentType(MediaType.APPLICATION_JSON)
                                        .bodyValue(news))
                .switchIfEmpty(notFound().build());
    }

    public Mono<ServerResponse> createNews(ServerRequest request) {
        return ok().body(
                newsService.create(
                        request.bodyToMono(NewsResource.class).map(newsResourceAssembler::toModel))
                        .map(newsResourceAssembler::toResource), NewsResource.class);
    }

    public Mono<ServerResponse> deleteNews(ServerRequest request) {
        return ok().build(newsService.deleteById(Long.valueOf(request.pathVariable("newsId"))));
    }

    public Mono<ServerResponse> updateNews(ServerRequest request) {
        Long newsId = Long.valueOf(request.pathVariable("newsId"));
        return ok().body(
                newsService.update(
                        request.bodyToMono(NewsResource.class).map(newsResourceAssembler::toModel), newsId)
                        .map(newsResourceAssembler::toResource), NewsResource.class);
    }

}
