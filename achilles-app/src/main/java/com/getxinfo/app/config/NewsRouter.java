package com.getxinfo.app.config;

import com.getxinfo.app.api.NewsHandler;
import com.getxinfo.app.api.UserHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;

@Configuration
public class NewsRouter {

    @Bean
    public RouterFunction<ServerResponse> newsRoute(NewsHandler newsHandler) {

        return RouterFunctions.route(GET("/news"), newsHandler::getAllNews)
                .andRoute(GET("/news/{newsId}"), newsHandler::getNews);
    }

}
