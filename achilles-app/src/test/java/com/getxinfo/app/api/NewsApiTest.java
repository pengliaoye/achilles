package com.getxinfo.app.api;

import com.getxinfo.app.MockServerContextInitializer;
import com.getxinfo.app.api.NewsHandler;
import com.getxinfo.app.api.UserHandler;
import com.getxinfo.app.config.NewsRouter;
import com.getxinfo.app.config.UserRouter;
import com.getxinfo.app.config.WebClientConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

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

}
