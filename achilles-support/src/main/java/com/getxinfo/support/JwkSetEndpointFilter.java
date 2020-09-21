package com.getxinfo.support;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;

@Component
public class JwkSetEndpointFilter implements WebFilter {
    static final String DEFAULT_JWK_SET_URI = "/oauth2/jwks";
    private final JWKSet jwkSet;

    public JwkSetEndpointFilter(@Value("classpath:key.json")
                                        Resource keyFile) {
        this.jwkSet = generateJwkSet(keyFile);
    }

    private JWKSet generateJwkSet(Resource keyFile) {
        try {
            String keyJson = StreamUtils.copyToString(keyFile.getInputStream(), StandardCharsets.UTF_8);
            RSAKey jwk = RSAKey.parse(keyJson);
            return new JWKSet(jwk);
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        HttpMethod method = request.getMethod();
        if (method.equals(HttpMethod.GET) && request.getPath().toString().equals(DEFAULT_JWK_SET_URI)) {
            String jwks = this.jwkSet.toPublicJWKSet().toJSONObject().toJSONString();
            byte[] bytes = jwks.getBytes(StandardCharsets.UTF_8);
            ServerHttpResponse response = exchange.getResponse();
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
        } else {
            return chain.filter(exchange);
        }
    }

}