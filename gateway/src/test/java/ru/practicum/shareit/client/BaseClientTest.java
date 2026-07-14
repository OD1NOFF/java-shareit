package ru.practicum.shareit.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class BaseClientTest {

    private RestTemplate restTemplate;
    private MockRestServiceServer mockServer;
    private TestClient client;

    private static class TestClient extends BaseClient {
        TestClient(RestTemplate rest) {
            super(rest);
        }

        ResponseEntity<Object> doGet(String path, long userId) {
            return get(path, userId);
        }

        ResponseEntity<Object> doGetWithParams(String path, long userId, Map<String, Object> params) {
            return get(path, userId, params);
        }

        <T> ResponseEntity<Object> doPost(String path, long userId, T body) {
            return post(path, userId, body);
        }

        <T> ResponseEntity<Object> doPatch(String path, long userId, T body) {
            return patch(path, userId, body);
        }

        ResponseEntity<Object> doDelete(String path) {
            return delete(path);
        }
    }

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.createServer(restTemplate);
        client = new TestClient(restTemplate);
    }

    @Test
    void get_shouldReturnOkResponse() {
        mockServer.expect(requestTo("/items/1"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andRespond(withSuccess("{\"id\":1}", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = client.doGet("/items/1", 1L);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    void post_shouldSendBodyAndReturnOk() {
        mockServer.expect(requestTo("/items"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("{\"id\":1}", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = client.doPost("/items", 1L, "{\"name\":\"Дрель\"}");

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    void patch_shouldReturnOk() {
        mockServer.expect(requestTo("/items/1"))
                .andExpect(method(HttpMethod.PATCH))
                .andRespond(withSuccess());

        ResponseEntity<Object> response = client.doPatch("/items/1", 1L, "{}");

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    void delete_shouldReturnOk() {
        mockServer.expect(requestTo("/users/1"))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withSuccess());

        ResponseEntity<Object> response = client.doDelete("/users/1");

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    void get_shouldPropagateServerErrorWithBody() {
        mockServer.expect(requestTo("/items/999"))
                .andRespond(withStatus(org.springframework.http.HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"error\":\"Не найдено\"}"));

        ResponseEntity<Object> response = client.doGet("/items/999", 1L);

        assertThat(response.getStatusCode().value()).isEqualTo(404);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void get_shouldReturn500OnConnectionFailure() {
        mockServer.expect(requestTo("/items/1"))
                .andRespond(request -> {
                    throw new java.io.IOException("Connection refused");
                });

        ResponseEntity<Object> response = client.doGet("/items/1", 1L);

        assertThat(response.getStatusCode().value()).isEqualTo(500);
    }

    @Test
    void get_shouldSendParametrizedRequest() {
        mockServer.expect(requestTo("/items/search?text=drel"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = client.doGetWithParams(
                "/items/search?text={text}", 1L, Map.of("text", "drel"));

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }
}