package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class ItemClientTest {

    private MockRestServiceServer mockServer;
    private ItemClient itemClient;

    @BeforeEach
    void setUp() {
        itemClient = new ItemClient("http://localhost:9090", new RestTemplateBuilder());
        RestTemplate restTemplate = (RestTemplate) ReflectionTestUtils.getField(itemClient, "rest");
        mockServer = MockRestServiceServer.bindTo(restTemplate).build();
    }

    @Test
    void create_shouldSendPostRequest() {
        mockServer.expect(requestTo("http://localhost:9090/items"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("{\"id\":1}", MediaType.APPLICATION_JSON));

        ItemDto dto = ItemDto.builder().name("Дрель").description("Мощная").available(true).build();
        ResponseEntity<Object> response = itemClient.create(1L, dto);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    void getById_shouldSendGetRequest() {
        mockServer.expect(requestTo("http://localhost:9090/items/1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{\"id\":1}", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = itemClient.getById(1L, 1L);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    void getAllByOwner_shouldSendGetRequest() {
        mockServer.expect(requestTo("http://localhost:9090/items"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = itemClient.getAllByOwner(1L);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    void search_shouldSendGetRequestWithTextParam() {
        mockServer.expect(requestTo("http://localhost:9090/items/search?text=drel"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = itemClient.search(1L, "drel");

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    void update_shouldSendPatchRequest() {
        mockServer.expect(requestTo("http://localhost:9090/items/1"))
                .andExpect(method(HttpMethod.PATCH))
                .andRespond(withSuccess("{\"id\":1}", MediaType.APPLICATION_JSON));

        ItemDto dto = ItemDto.builder().name("Новое").build();
        ResponseEntity<Object> response = itemClient.update(1L, 1L, dto);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    void addComment_shouldSendPostRequest() {
        mockServer.expect(requestTo("http://localhost:9090/items/1/comment"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("{\"id\":1}", MediaType.APPLICATION_JSON));

        CommentRequestDto dto = new CommentRequestDto("Отлично!");
        ResponseEntity<Object> response = itemClient.addComment(1L, 1L, dto);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }
}