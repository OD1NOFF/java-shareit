package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;

    @Test
    void create_shouldReturnCreatedBooking() throws Exception {
        BookingRequestDto request = BookingRequestDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        BookingDto response = BookingDto.builder().id(1L).status(BookingStatus.WAITING).build();

        when(bookingService.create(1L, request)).thenReturn(response);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("WAITING")));
    }

    @Test
    void approve_shouldReturnApprovedBooking() throws Exception {
        BookingDto response = BookingDto.builder().id(1L).status(BookingStatus.APPROVED).build();

        when(bookingService.approve(1L, 1L, true)).thenReturn(response);

        mvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("APPROVED")));
    }

    @Test
    void getById_shouldReturnBooking() throws Exception {
        BookingDto response = BookingDto.builder().id(1L).status(BookingStatus.WAITING).build();

        when(bookingService.getById(1L, 1L)).thenReturn(response);

        mvc.perform(get("/bookings/1").header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void getById_shouldReturn404WhenNotFound() throws Exception {
        when(bookingService.getById(1L, 999L)).thenThrow(new NotFoundException("Не найдено"));

        mvc.perform(get("/bookings/999").header("X-Sharer-User-Id", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllByBooker_shouldReturnListWithDefaultState() throws Exception {
        BookingDto response = BookingDto.builder().id(1L).status(BookingStatus.WAITING).build();

        when(bookingService.getAllByBooker(1L, BookingState.ALL)).thenReturn(List.of(response));

        mvc.perform(get("/bookings").header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getAllByOwner_shouldReturnList() throws Exception {
        BookingDto response = BookingDto.builder().id(1L).status(BookingStatus.WAITING).build();

        when(bookingService.getAllByOwner(1L, BookingState.ALL)).thenReturn(List.of(response));

        mvc.perform(get("/bookings/owner").header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getAllByBooker_shouldReturn400ForInvalidState() throws Exception {
        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "UNKNOWN_STATE"))
                .andExpect(status().isBadRequest());
    }
}