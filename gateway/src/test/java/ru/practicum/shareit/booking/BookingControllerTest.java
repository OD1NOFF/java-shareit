package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingClient bookingClient;

    @Test
    void create_shouldReturnOkWhenValid() throws Exception {
        BookItemRequestDto request = new BookItemRequestDto(
                1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));

        when(bookingClient.create(1L, request)).thenReturn(ResponseEntity.ok().build());

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void create_shouldReturn400WhenItemIdNull() throws Exception {
        BookItemRequestDto invalid = new BookItemRequestDto(
                null, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_shouldReturn400WhenEndBeforeStart() throws Exception {
        BookItemRequestDto invalid = new BookItemRequestDto(
                1L, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(1));

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void approve_shouldReturnOk() throws Exception {
        when(bookingClient.approve(1L, 1L, true)).thenReturn(ResponseEntity.ok().build());

        mvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk());
    }

    @Test
    void getById_shouldReturnOk() throws Exception {
        when(bookingClient.getById(1L, 1L)).thenReturn(ResponseEntity.ok().build());

        mvc.perform(get("/bookings/1").header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getAllByBooker_shouldReturnOkWithDefaultState() throws Exception {
        when(bookingClient.getAllByBooker(1L, BookingState.ALL)).thenReturn(ResponseEntity.ok().build());

        mvc.perform(get("/bookings").header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getAllByBooker_shouldReturn400ForInvalidState() throws Exception {
        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "НЕИЗВЕСТНО"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllByOwner_shouldReturnOk() throws Exception {
        when(bookingClient.getAllByOwner(1L, BookingState.ALL)).thenReturn(ResponseEntity.ok().build());

        mvc.perform(get("/bookings/owner").header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }
}