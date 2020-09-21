package com.getxinfo.content.config;

import com.getxinfo.content.api.NewsHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class NewsRouter {

    @Bean
    public RouterFunction<ServerResponse> route(NewsHandler newsHandler) {

        return RouterFunctions.route(GET("/news"),
                newsHandler::getAllNews)
                .andRoute(POST("/news")
                        .and(contentType(MediaType.APPLICATION_JSON)), newsHandler::createNews)
                .andRoute(GET("/news/{newsId}"),
                        newsHandler::getNews)
                .andRoute(PUT("/news/{newsId}")
                        .and(contentType(MediaType.APPLICATION_JSON)), newsHandler::updateNews)
                .andRoute(DELETE("/news/{newsId}"), newsHandler::deleteNews);
    }

}
