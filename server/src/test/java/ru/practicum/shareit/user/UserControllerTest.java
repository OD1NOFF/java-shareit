package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserService userService;

    @Test
    void create_shouldReturnCreatedUser() throws Exception {
        UserDto request = UserDto.builder().name("Иван").email("ivan@test.ru").build();
        UserDto response = UserDto.builder().id(1L).name("Иван").email("ivan@test.ru").build();

        when(userService.create(request)).thenReturn(response);

        mvc.perform(post("/users")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Иван")));
    }

    @Test
    void create_shouldReturn400WhenEmailBlank() throws Exception {
        UserDto invalid = UserDto.builder().name("Иван").build();

        mvc.perform(post("/users")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_shouldReturn409OnConflict() throws Exception {
        UserDto request = UserDto.builder().name("Иван").email("ivan@test.ru").build();

        when(userService.create(request)).thenThrow(new ConflictException("Email занят"));

        mvc.perform(post("/users")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void update_shouldReturnUpdatedUser() throws Exception {
        UserDto request = UserDto.builder().name("Новое имя").build();
        UserDto response = UserDto.builder().id(1L).name("Новое имя").email("ivan@test.ru").build();

        when(userService.update(1L, request)).thenReturn(response);

        mvc.perform(patch("/users/1")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Новое имя")));
    }

    @Test
    void getById_shouldReturnUser() throws Exception {
        UserDto response = UserDto.builder().id(1L).name("Иван").email("ivan@test.ru").build();

        when(userService.getById(1L)).thenReturn(response);

        mvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("ivan@test.ru")));
    }

    @Test
    void getById_shouldReturn404WhenNotFound() throws Exception {
        when(userService.getById(999L)).thenThrow(new NotFoundException("Не найден"));

        mvc.perform(get("/users/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAll_shouldReturnListOfUsers() throws Exception {
        UserDto user = UserDto.builder().id(1L).name("Иван").email("ivan@test.ru").build();

        when(userService.getAll()).thenReturn(List.of(user));

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(1)));
    }

    @Test
    void delete_shouldReturnOk() throws Exception {
        mvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
    }
}