package com.getxinfo.support.api;

import com.getxinfo.support.JwkSetEndpointFilter;
import com.getxinfo.support.business.UserService;
import com.getxinfo.support.dataaccess.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest
@Import({
        JwkSetEndpointFilter.class,
        UserService.class})
@WithMockUser
public class JwkSetFilterTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private StringRedisTemplate redisTemplate;

    @MockBean
    private UserRepository userRepository;

    @Test
    @DisplayName("验证jwt")
    void verifyJwtFilter() {
        webTestClient
                .get()
                .uri("/oauth2/jwks")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .returnResult();
        ;
    }

}
