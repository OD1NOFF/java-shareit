package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserClient userClient;

    @Test
    void create_shouldReturnOkWhenValid() throws Exception {
        UserDto request = UserDto.builder().name("Иван").email("ivan@test.ru").build();

        when(userClient.create(request)).thenReturn(ResponseEntity.ok().build());

        mvc.perform(post("/users")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk());
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
    void create_shouldReturn400WhenNameBlank() throws Exception {
        UserDto invalid = UserDto.builder().email("ivan@test.ru").build();

        mvc.perform(post("/users")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_shouldReturn400WhenEmailInvalid() throws Exception {
        UserDto invalid = UserDto.builder().name("Иван").email("не-email").build();

        mvc.perform(post("/users")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_shouldReturnOk() throws Exception {
        UserDto request = UserDto.builder().name("Новое имя").build();

        when(userClient.update(1L, request)).thenReturn(ResponseEntity.ok().build());

        mvc.perform(patch("/users/1")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void getById_shouldReturnOk() throws Exception {
        when(userClient.getById(1L)).thenReturn(ResponseEntity.ok().build());

        mvc.perform(get("/users/1"))
                .andExpect(status().isOk());
    }

    @Test
    void getAll_shouldReturnOk() throws Exception {
        when(userClient.getAll()).thenReturn(ResponseEntity.ok().build());

        mvc.perform(get("/users"))
                .andExpect(status().isOk());
    }

    @Test
    void delete_shouldReturnOk() throws Exception {
        when(userClient.delete(1L)).thenReturn(ResponseEntity.status(HttpStatus.OK).build());

        mvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
    }
}