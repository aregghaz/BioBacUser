package com.biobac.users;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                // Use in-memory H2 for tests to avoid MySQL dependency
                "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=MySQL",
                "spring.datasource.driver-class-name=org.h2.Driver",
                "spring.datasource.username=sa",
                "spring.datasource.password=",
                "spring.jpa.hibernate.ddl-auto=create-drop"
        })
class OpenApiDocsTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate rest;

    @Test
    void apiDocsEndpointShouldReturn200() {
        String url = "http://localhost:" + port + "/v3/api-docs";
        ResponseEntity<String> response = rest.getForEntity(url, String.class);
        // For debugging, ensure we see response in case of failure
        System.out.println("[DEBUG_LOG] /v3/api-docs status=" + response.getStatusCode() + ", body length=" + (response.getBody() == null ? 0 : response.getBody().length()));
        assertThat(response.getStatusCode().is2xxSuccessful())
                .as("Expected 2xx from /v3/api-docs, got: %s, body: %s", response.getStatusCode(), response.getBody())
                .isTrue();
    }
}
