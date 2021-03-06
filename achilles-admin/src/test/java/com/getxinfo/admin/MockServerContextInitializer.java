package com.getxinfo.admin;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.support.TestPropertySourceUtils;

public class MockServerContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final MockWebServer mockWebServer = new MockWebServer();
    private static final Dispatcher dispatcher = new Dispatcher() {

        @Override
        public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
            MockResponse mockResponse = null;
            switch (request.getPath()) {
                case "/news":
                    if (request.getMethod().equals("POST")) {
                        mockResponse = new MockResponse()
                                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .setBody("{\"id\":1,\"title\":\"90周年阅兵\",\"content\":\"阅兵\"}");
                    } else {
                        mockResponse = new MockResponse()
                                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .setBody("[{\"id\":1,\"title\":\"90周年阅兵\",\"content\":\"阅兵\"}]");
                    }
                    break;
                case "/news/1":
                    mockResponse = new MockResponse()
                            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                            .setBody("{\"id\":1,\"title\":\"90周年阅兵\",\"content\":\"阅兵\"}");
                    break;
                default:
                    mockResponse = new MockResponse().setResponseCode(404);
                    break;
            }
            return mockResponse;
        }
    };

    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        mockWebServer.setDispatcher(dispatcher);
        String url = mockWebServer.url("/").toString();
        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(configurableApplicationContext,
                "supoort-api.url=" + url
                , "content-api.url=" + url);
    }
}
