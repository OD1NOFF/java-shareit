package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemService itemService;

    @Test
    void create_shouldReturnCreatedItem() throws Exception {
        ItemDto request = ItemDto.builder().name("Дрель").description("Мощная").available(true).build();
        ItemDto response = ItemDto.builder().id(1L).name("Дрель").description("Мощная").available(true).build();

        when(itemService.create(1L, request)).thenReturn(response);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
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
    void update_shouldReturn403WhenNotOwner() throws Exception {
        ItemDto request = ItemDto.builder().name("Новое").build();

        when(itemService.update(2L, 1L, request)).thenThrow(new ForbiddenException("Не владелец"));

        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 2L)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void getById_shouldReturnItem() throws Exception {
        ItemDto response = ItemDto.builder().id(1L).name("Дрель").available(true).build();

        when(itemService.getById(1L, 1L)).thenReturn(response);

        mvc.perform(get("/items/1").header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Дрель")));
    }

    @Test
    void getAllByOwner_shouldReturnList() throws Exception {
        ItemDto item = ItemDto.builder().id(1L).name("Дрель").available(true).build();

        when(itemService.getAllByOwner(1L)).thenReturn(List.of(item));

        mvc.perform(get("/items").header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void search_shouldReturnMatchingItems() throws Exception {
        ItemDto item = ItemDto.builder().id(1L).name("Дрель").available(true).build();

        when(itemService.search("дрель")).thenReturn(List.of(item));

        mvc.perform(get("/items/search").param("text", "дрель"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void addComment_shouldReturnCreatedComment() throws Exception {
        CommentDto request = CommentDto.builder().text("Отлично!").build();
        CommentDto response = CommentDto.builder().id(1L).text("Отлично!").authorName("Иван").build();

        when(itemService.addComment(1L, 1L, request)).thenReturn(response);

        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is("Отлично!")));
    }

    @Test
    void addComment_shouldReturn400WhenTextBlank() throws Exception {
        CommentDto invalid = CommentDto.builder().text("").build();

        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }
}