package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Test
    void create_shouldReturnCreatedRequest() throws Exception {
        ItemRequestDto request = ItemRequestDto.builder().description("Нужна дрель").build();
        ItemRequestResponseDto response = ItemRequestResponseDto.builder()
                .id(1L).description("Нужна дрель").items(List.of()).build();

        when(itemRequestService.create(1L, request)).thenReturn(response);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is("Нужна дрель")));
    }

    @Test
    void getOwnRequests_shouldReturnList() throws Exception {
        ItemRequestResponseDto response = ItemRequestResponseDto.builder()
                .id(1L).description("Нужна дрель").items(List.of()).build();

        when(itemRequestService.getOwnRequests(1L)).thenReturn(List.of(response));

        mvc.perform(get("/requests").header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getAllRequests_shouldReturnList() throws Exception {
        ItemRequestResponseDto response = ItemRequestResponseDto.builder()
                .id(1L).description("Нужна дрель").items(List.of()).build();

        when(itemRequestService.getAllRequests(1L)).thenReturn(List.of(response));

        mvc.perform(get("/requests/all").header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getById_shouldReturnRequest() throws Exception {
        ItemRequestResponseDto response = ItemRequestResponseDto.builder()
                .id(1L).description("Нужна дрель").items(List.of()).build();

        when(itemRequestService.getById(1L, 1L)).thenReturn(response);

        mvc.perform(get("/requests/1").header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }
}