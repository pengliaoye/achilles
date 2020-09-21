package com.getxinfo.support.api;

import com.getxinfo.support.Constants;
import com.getxinfo.support.business.UserService;
import com.getxinfo.support.dataaccess.User;
import com.getxinfo.support.dataaccess.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.documentationConfiguration;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebFluxTest
@Import({
        RedisAutoConfiguration.class,
        UserService.class})
@WithMockUser
@DisplayName("用户api")
public class UserControllerTest {

    @Autowired
    private ApplicationContext applicationContext;

    private WebTestClient webTestClient;

    @MockBean
    private UserRepository userRepository;
    @MockBean
    private StringRedisTemplate redisTemplate;
    private final ValueOperations mockedValueOperations = Mockito.mock(ValueOperations.class);

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        this.webTestClient =
                WebTestClient.bindToApplicationContext(applicationContext)
                        .configureClient()
                        .baseUrl("http://localhost:8080")
                        .filter(
                                documentationConfiguration(restDocumentation)
                                        .operationPreprocessors()
                                        .withRequestDefaults(prettyPrint())
                                        .withResponseDefaults(prettyPrint()))
                        .build();

        when(redisTemplate.opsForValue()).thenReturn(mockedValueOperations);
    }

    @Test
    @DisplayName("发送短信验证码")
    void verifySendSmscode() {
        String telphone = "13312345678";

        webTestClient
                .mutateWith(csrf())
                .get()
                .uri("/users/smscode?swissNumberStr=" + telphone)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.data.code")
                .hasJsonPath()
                .consumeWith(document("send-code"
                ));
    }

    @Test
    @DisplayName("验证短信验证码")
    void verifyCheckCode() {
        String telphone = "13312345678";
        String code = "123456";
        Long id = 1L;
        User user = new User();
        user.setId(id);
        given(userRepository.findByTelphone(telphone)).willReturn(user);
        given(mockedValueOperations.get(Constants.SMS_CODE_PREFIX + telphone)).willReturn(code);

        webTestClient
                .mutateWith(csrf())
                .get()
                .uri("/users/auth?telphone=" + telphone + "&code=" + code)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.access_token")
                .hasJsonPath()
                .consumeWith(document("verify-code"
                ));
    }

    @Test
    @DisplayName("错误短信验证码")
    void verifyErrorCode() {
        String telphone = "13312345678";
        String code = "123456";
        Long id = 1L;
        User user = new User();
        user.setId(id);
        given(userRepository.findByTelphone(telphone)).willReturn(user);
        given(mockedValueOperations.get(Constants.SMS_CODE_PREFIX + telphone)).willReturn("123");

        webTestClient
                .mutateWith(csrf())
                .get()
                .uri("/users/auth?telphone=" + telphone + "&code=" + code)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.error_code")
                .isEqualTo("A0131");
    }

}
