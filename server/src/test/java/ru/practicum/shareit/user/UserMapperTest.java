package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    @Test
    void toUserDto_shouldMapAllFields() {
        User user = new User();
        user.setId(1L);
        user.setName("Иван");
        user.setEmail("ivan@test.ru");

        UserDto dto = UserMapper.toUserDto(user);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Иван");
        assertThat(dto.getEmail()).isEqualTo("ivan@test.ru");
    }

    @Test
    void toUser_shouldMapAllFields() {
        UserDto dto = UserDto.builder()
                .id(1L)
                .name("Иван")
                .email("ivan@test.ru")
                .build();

        User user = UserMapper.toUser(dto);

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getName()).isEqualTo("Иван");
        assertThat(user.getEmail()).isEqualTo("ivan@test.ru");
    }
}