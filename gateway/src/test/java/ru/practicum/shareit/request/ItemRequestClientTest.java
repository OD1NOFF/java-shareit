package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class ItemRequestClientTest {

    private MockRestServiceServer mockServer;
    private ItemRequestClient itemRequestClient;

    @BeforeEach
    void setUp() {
        itemRequestClient = new ItemRequestClient("http://localhost:9090", new RestTemplateBuilder());
        RestTemplate restTemplate = (RestTemplate) ReflectionTestUtils.getField(itemRequestClient, "rest");
        mockServer = MockRestServiceServer.bindTo(restTemplate).build();
    }

    @Test
    void create_shouldSendPostRequest() {
        mockServer.expect(requestTo("http://localhost:9090/requests"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("{\"id\":1}", MediaType.APPLICATION_JSON));

        ItemRequestDto dto = ItemRequestDto.builder().description("Нужна дрель").build();
        ResponseEntity<Object> response = itemRequestClient.create(1L, dto);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    void getOwnRequests_shouldSendGetRequest() {
        mockServer.expect(requestTo("http://localhost:9090/requests"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = itemRequestClient.getOwnRequests(1L);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    void getAllRequests_shouldSendGetRequest() {
        mockServer.expect(requestTo("http://localhost:9090/requests/all"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = itemRequestClient.getAllRequests(1L);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    void getById_shouldSendGetRequest() {
        mockServer.expect(requestTo("http://localhost:9090/requests/1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{\"id\":1}", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = itemRequestClient.getById(1L, 1L);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }
}