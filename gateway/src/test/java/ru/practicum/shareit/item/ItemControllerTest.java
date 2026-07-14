package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemClient itemClient;

    @Test
    void create_shouldReturnOkWhenValid() throws Exception {
        ItemDto request = ItemDto.builder().name("Дрель").description("Мощная").available(true).build();

        when(itemClient.create(1L, request)).thenReturn(ResponseEntity.ok().build());

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void create_shouldReturn400WhenNameBlank() throws Exception {
        ItemDto invalid = ItemDto.builder().description("Мощная").available(true).build();

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_shouldReturn400WhenDescriptionBlank() throws Exception {
        ItemDto invalid = ItemDto.builder().name("Дрель").available(true).build();

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_shouldReturn400WhenAvailableNull() throws Exception {
        ItemDto invalid = ItemDto.builder().name("Дрель").description("Мощная").build();

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_shouldReturnOk() throws Exception {
        ItemDto request = ItemDto.builder().name("Новое").build();

        when(itemClient.update(1L, 1L, request)).thenReturn(ResponseEntity.ok().build());

        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void getById_shouldReturnOk() throws Exception {
        when(itemClient.getById(1L, 1L)).thenReturn(ResponseEntity.ok().build());

        mvc.perform(get("/items/1").header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getAllByOwner_shouldReturnOk() throws Exception {
        when(itemClient.getAllByOwner(1L)).thenReturn(ResponseEntity.ok().build());

        mvc.perform(get("/items").header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void search_shouldReturnOk() throws Exception {
        when(itemClient.search(1L, "дрель")).thenReturn(ResponseEntity.ok().build());

        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1L)
                        .param("text", "дрель"))
                .andExpect(status().isOk());
    }

    @Test
    void addComment_shouldReturnOk() throws Exception {
        CommentRequestDto request = new CommentRequestDto("Отлично!");

        when(itemClient.addComment(1L, 1L, request)).thenReturn(ResponseEntity.ok().build());

        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void addComment_shouldReturn400WhenTextBlank() throws Exception {
        CommentRequestDto invalid = new CommentRequestDto("");

        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }
}