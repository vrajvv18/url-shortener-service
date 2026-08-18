package com.example.urlshortener;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import java.io.IOException;
import java.net.HttpURLConnection;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UrlShortenerIntegrationTest {
    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("urlshortener")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void database(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("app.public-url.base-url", () -> "http://localhost:8080");
    }

    @Autowired
    TestRestTemplate rest;

    @BeforeEach
    void disableRedirects() {
        rest.getRestTemplate().setRequestFactory(
                new SimpleClientHttpRequestFactory() {
                    @Override
                    protected void prepareConnection(
                            HttpURLConnection connection,
                            String httpMethod) throws IOException {
                        super.prepareConnection(connection, httpMethod);
                        connection.setInstanceFollowRedirects(false);
                    }
                }
        );
    }

    @LocalServerPort int port;

    @Test
    void createRedirectAnalyticsAndDisableFlowWorks() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> create = new HttpEntity<>(Map.of("longUrl", "https://example.com/products/123"), headers);

        ResponseEntity<Map> created = rest.postForEntity("http://localhost:" + port + "/api/v1/urls", create, Map.class);
        assertEquals(HttpStatus.CREATED, created.getStatusCode());
        String code = (String) created.getBody().get("shortCode");
        assertNotNull(code);

        ResponseEntity<Void> redirect = rest.getForEntity("http://localhost:" + port + "/" + code, Void.class);
        assertEquals(HttpStatus.FOUND, redirect.getStatusCode());
        assertNotNull(redirect.getHeaders().getLocation());
        assertEquals("https://example.com/products/123", redirect.getHeaders().getLocation().toString());


        ResponseEntity<Map> analytics = rest.getForEntity("http://localhost:" + port + "/api/v1/urls/" + code + "/analytics", Map.class);
        assertEquals(HttpStatus.OK, analytics.getStatusCode());
        assertEquals(1, ((Number) analytics.getBody().get("clicks")).intValue());

        rest.delete("http://localhost:" + port + "/api/v1/urls/" + code);
        ResponseEntity<String> disabled = rest.getForEntity("http://localhost:" + port + "/" + code, String.class);
        assertEquals(HttpStatus.GONE, disabled.getStatusCode());
    }

    @Test
    void expiredUrlReturnsGone() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> create = new HttpEntity<>(Map.of(
                "longUrl", "https://example.com/expired",
                "expiresAt", "2020-01-01T00:00:00Z"), headers);
        ResponseEntity<Map> created = rest.postForEntity("http://localhost:" + port + "/api/v1/urls", create, Map.class);
        assertEquals(HttpStatus.BAD_REQUEST, created.getStatusCode());
    }

    @Test
    void invalidUrlReturnsBadRequest() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(Map.of("longUrl", "ftp://example.com"), headers);
        ResponseEntity<Map> response = rest.postForEntity("http://localhost:" + port + "/api/v1/urls", request, Map.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
