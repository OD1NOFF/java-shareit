package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class BookingClientTest {

    private MockRestServiceServer mockServer;
    private BookingClient bookingClient;

    @BeforeEach
    void setUp() {
        bookingClient = new BookingClient("http://localhost:9090", new RestTemplateBuilder());
        RestTemplate restTemplate = (RestTemplate) ReflectionTestUtils.getField(bookingClient, "rest");
        mockServer = MockRestServiceServer.bindTo(restTemplate).build();
    }

    @Test
    void create_shouldSendPostRequest() {
        mockServer.expect(requestTo("http://localhost:9090/bookings"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("{\"id\":1}", MediaType.APPLICATION_JSON));

        BookItemRequestDto dto = new BookItemRequestDto(
                1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        ResponseEntity<Object> response = bookingClient.create(1L, dto);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    void approve_shouldSendPatchRequestWithParam() {
        mockServer.expect(requestTo("http://localhost:9090/bookings/1?approved=true"))
                .andExpect(method(HttpMethod.PATCH))
                .andRespond(withSuccess("{\"id\":1}", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = bookingClient.approve(1L, 1L, true);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    void getById_shouldSendGetRequest() {
        mockServer.expect(requestTo("http://localhost:9090/bookings/1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{\"id\":1}", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = bookingClient.getById(1L, 1L);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    void getAllByBooker_shouldSendGetRequestWithState() {
        mockServer.expect(requestTo("http://localhost:9090/bookings?state=ALL"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = bookingClient.getAllByBooker(1L, BookingState.ALL);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    void getAllByOwner_shouldSendGetRequestWithState() {
        mockServer.expect(requestTo("http://localhost:9090/bookings/owner?state=ALL"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = bookingClient.getAllByOwner(1L, BookingState.ALL);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }
}