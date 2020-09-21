package com.getxinfo.admin.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.getxinfo.admin.MockServerContextInitializer;
import com.getxinfo.admin.config.NewsRouter;
import com.getxinfo.admin.config.WebClientConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.documentationConfiguration;

@ExtendWith({RestDocumentationExtension.class})
@WebFluxTest
@Import({
        NewsRouter.class,
        NewsHandler.class,
        WebClientConfig.class
})
@ContextConfiguration(
        initializers = MockServerContextInitializer.class)
public class NewsApiTest {

    @Autowired
    private ApplicationContext applicationContext;
    private WebTestClient webTestClient;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        this.webTestClient =
                WebTestClient.bindToApplicationContext(applicationContext)
                        .configureClient()
                        .baseUrl("http://localhost:9010")
                        .filter(
                                documentationConfiguration(restDocumentation)
                                        .operationPreprocessors()
                                        .withRequestDefaults(prettyPrint())
                                        .withResponseDefaults(prettyPrint()))
                        .build();
    }

    @Test
    public void testGetAllNews() throws InterruptedException {

        webTestClient.get().uri("/news")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .json("[{\"id\":1,\"title\":\"90周年阅兵\",\"content\":\"阅兵\"}]")
                .consumeWith(document("list-news"));
    }

    @Test
    public void testGetNews() throws InterruptedException {

        webTestClient.get().uri("/news/1")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.id")
                .isEqualTo(1)
                .consumeWith(document("get-news"));
    }

    @Test
    public void testCreateNews() throws JsonProcessingException {
        Map<String, Object> map = new HashMap<>();
        map.put("title", "90周年阅兵");
        map.put("content", "阅兵");
        webTestClient
                .post()
                .uri("/news")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(map))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .json("{\"id\":1,\"title\":\"90周年阅兵\",\"content\":\"阅兵\"}")
                .consumeWith(document("create-news"));
    }

    @Test
    void testUpdateNews() throws JsonProcessingException {
        long id = 1L;
        Map<String, Object> map = new HashMap<>();
        map.put("title", "90周年阅兵");
        map.put("content", "阅兵");
        webTestClient
                .put()
                .uri("/news/" + id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(map))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .json("{\"id\":1,\"title\":\"90周年阅兵\",\"content\":\"阅兵\"}")
                .consumeWith(document("update-news"));
    }


}
