package com.getxinfo.app.api;

import com.getxinfo.app.MockServerContextInitializer;
import com.getxinfo.app.api.NewsHandler;
import com.getxinfo.app.api.UserHandler;
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
        UserRouter.class,
        UserHandler.class,
        WebClientConfig.class
})
@ContextConfiguration(
        initializers = MockServerContextInitializer.class)
public class UserApiTest {

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
    public void testSendSmsCode() throws InterruptedException {

        webTestClient.get().uri("/users/smscode?telephone=13312345678")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.data.code")
                .hasJsonPath()
                .consumeWith(document("send-code"));
    }

    @Test
    public void testAuthentication() throws InterruptedException {

        webTestClient.get().uri("/users/auth?telephone=13312345678&code=123")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.data.access_token")
                .hasJsonPath()
                .consumeWith(document("auth"));
    }

}
