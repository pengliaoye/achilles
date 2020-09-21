package com.getxinfo.content.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.getxinfo.content.business.NewsService;
import com.getxinfo.content.config.ModelMapperConfiguration;
import com.getxinfo.content.config.NewsRouter;
import com.getxinfo.content.dataaccess.News;
import com.getxinfo.content.dataaccess.NewsBuilder;
import com.getxinfo.content.dataaccess.NewsRepository;
import org.elasticsearch.action.delete.DeleteRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.elasticsearch.ReactiveElasticsearchRestClientAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.data.elasticsearch.client.reactive.ReactiveElasticsearchClient;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.documentationConfiguration;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebFluxTest
@Import({ModelMapperConfiguration.class,
        ReactiveElasticsearchRestClientAutoConfiguration.class,
        NewsRouter.class,
        NewsHandler.class,
        NewsResourceAssembler.class,
        NewsService.class})
@DisplayName("验证新闻api")
public class NewsApiDocumentationTest {

    @Autowired
    private ApplicationContext applicationContext;

    private WebTestClient webTestClient;

    @MockBean
    private NewsRepository newsRepository;

    @MockBean
    private ReactiveElasticsearchClient elasticsearchClient;

    @Autowired
    private ObjectMapper objectMapper;

    FieldDescriptor[] fieldDescriptors = new FieldDescriptor[]{
            fieldWithPath("id").optional()
                    .type(Long.class.getSimpleName())
                    .description("ID"),
            fieldWithPath("title").description("Title of the news"),
            fieldWithPath("content").description("Content of the news"),
            fieldWithPath("createdAt").description("Created Time"),
            fieldWithPath("updatedAt").description("Updated Time"),
            fieldWithPath("deleted").description("Is Deleted")};

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        this.webTestClient =
                WebTestClient.bindToApplicationContext(applicationContext)
                        .configureClient()
                        .baseUrl("http://localhost:9020")
                        .filter(
                                documentationConfiguration(restDocumentation)
                                        .operationPreprocessors()
                                        .withRequestDefaults(prettyPrint())
                                        .withResponseDefaults(prettyPrint()))
                        .build();
    }

    @Test
    @DisplayName("获取新闻列表")
    void verifyAndDocumentGetNews() {
        Long id = 1L;
        News news = NewsBuilder.news().withId(id).build();
//        given(newsRepository.findAll()).willReturn(Arrays.asList(news));
//
//        webTestClient
//                .get()
//                .uri("/news?page=1&size=10")
//                .accept(MediaType.APPLICATION_JSON)
//                .exchange()
//                .expectStatus()
//                .isOk()
//                .expectBody()
//                .json("[{\"id\":" + id + "," +
//                        "\"title\":\"title\"," +
//                        "\"content\":\"content\"," +
//                        "\"createdAt\":\"2007-12-03T10:15:30\"," +
//                        "\"updatedAt\":\"2007-12-03T10:15:30\"," +
//                        "\"deleted\":true}]")
//                .consumeWith(document("list-news",
//                        requestParameters(
//                                parameterWithName("page").description("The page to retrieve"),
//                                parameterWithName("size").description("Entries per page")
//                        ),
//                        responseFields(
//                                fieldWithPath("[]").description("An array of news"))
//                                .andWithPrefix("[].", fieldDescriptors)));
    }

    @Test
    @DisplayName("获取单条新闻")
    void verifyAndDocumentGetSingleNews() {
        Long id = 1L;
        News news = NewsBuilder.news().withId(id).build();
        given(newsRepository.findById(id))
                .willReturn(Optional.of(news));

        webTestClient
                .get()
                .uri("/news/{newsId}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .json(
                        "{\"id\":" + id + "," +
                                "\"title\":\"title\"," +
                                "\"content\":\"content\"," +
                                "\"createdAt\":\"2007-12-03T10:15:30\"," +
                                "\"updatedAt\":\"2007-12-03T10:15:30\"," +
                                "\"deleted\":true}")
                .consumeWith(document("get-news"));
    }

    @Test
    @DisplayName("删除新闻")
    void verifyAndDocumentDeleteNews() {
        given(elasticsearchClient.delete(any(DeleteRequest.class)))
                .willReturn(Mono.empty());

        Long id = 1L;
        webTestClient
                .delete()
                .uri("/news/{newsId}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .consumeWith(document("delete-news",
                        pathParameters(
                                parameterWithName("newsId").description("新闻id"))));
    }

    @Test
    @DisplayName("新增新闻")
    void verifyAndDocumentCreateNews() throws JsonProcessingException {
        long id = 1L;
        News expectedNews = NewsBuilder.news().withId(id).build();

        NewsResource newsResource = new NewsResource(
                expectedNews.getId(),
                expectedNews.getTitle(),
                expectedNews.getContent(),
                expectedNews.getCreatedAt(),
                expectedNews.getUpdatedAt(),
                expectedNews.isDeleted());
        given(newsRepository.save(any())).willAnswer(i -> expectedNews);

        webTestClient
                .post()
                .uri("/news")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(newsResource))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .consumeWith(document("create-news", requestFields(fieldDescriptors)));

        ArgumentCaptor<News> userArg = ArgumentCaptor.forClass(News.class);
        verify(newsRepository).save(userArg.capture());
        assertThat(userArg.getValue()).isNotNull().isEqualTo(expectedNews);
    }

    @Test
    @DisplayName("更新新闻")
    void verifyAndDocumentUpdateNews() throws JsonProcessingException {
        long id = 1L;
        News expectedNews = NewsBuilder.news().build();

        NewsResource newsResource = new NewsResource(
                expectedNews.getId(),
                expectedNews.getTitle(),
                expectedNews.getContent(),
                expectedNews.getCreatedAt(),
                expectedNews.getUpdatedAt(),
                expectedNews.isDeleted());
        given(newsRepository.save(any())).willAnswer(i -> {
            expectedNews.setId(id);
            return expectedNews;
        });

        webTestClient
                .put()
                .uri("/news/" + id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(newsResource))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .consumeWith(document("update-news", requestFields(fieldDescriptors)));

        expectedNews.setId(id);
        ArgumentCaptor<News> userArg = ArgumentCaptor.forClass(News.class);
        verify(newsRepository).save(userArg.capture());
        assertThat(userArg.getValue()).isNotNull().isEqualTo(expectedNews);
    }

}
