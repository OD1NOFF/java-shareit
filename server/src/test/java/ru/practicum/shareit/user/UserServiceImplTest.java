package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceImplTest {

    @Autowired
    private UserService userService;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = UserDto.builder()
                .name("Иван Иванов")
                .email("ivan@test.ru")
                .build();
    }

    @Test
    void create_shouldSaveUserAndReturnDtoWithId() {
        UserDto created = userService.create(userDto);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("Иван Иванов");
        assertThat(created.getEmail()).isEqualTo("ivan@test.ru");
    }

    @Test
    void create_shouldThrowConflictWhenEmailExists() {
        userService.create(userDto);

        UserDto duplicate = UserDto.builder()
                .name("Другой")
                .email("ivan@test.ru")
                .build();

        assertThrows(ConflictException.class, () -> userService.create(duplicate));
    }

    @Test
    void update_shouldUpdateNameAndEmail() {
        UserDto created = userService.create(userDto);

        UserDto updateDto = UserDto.builder()
                .name("Пётр Петров")
                .email("petr@test.ru")
                .build();

        UserDto updated = userService.update(created.getId(), updateDto);

        assertThat(updated.getName()).isEqualTo("Пётр Петров");
        assertThat(updated.getEmail()).isEqualTo("petr@test.ru");
    }

    @Test
    void update_shouldUpdateOnlyNameWhenEmailNotProvided() {
        UserDto created = userService.create(userDto);

        UserDto updateDto = UserDto.builder()
                .name("Только имя")
                .build();

        UserDto updated = userService.update(created.getId(), updateDto);

        assertThat(updated.getName()).isEqualTo("Только имя");
        assertThat(updated.getEmail()).isEqualTo("ivan@test.ru");
    }

    @Test
    void update_shouldThrowNotFoundWhenUserDoesNotExist() {
        UserDto updateDto = UserDto.builder().name("Ктото").build();

        assertThrows(NotFoundException.class, () -> userService.update(999L, updateDto));
    }

    @Test
    void update_shouldThrowConflictWhenNewEmailBelongsToAnotherUser() {
        UserDto first = userService.create(userDto);
        UserDto second = userService.create(UserDto.builder()
                .name("Второй")
                .email("second@test.ru")
                .build());

        UserDto updateDto = UserDto.builder().email("second@test.ru").build();

        assertThrows(ConflictException.class, () -> userService.update(first.getId(), updateDto));
    }

    @Test
    void getById_shouldReturnUser() {
        UserDto created = userService.create(userDto);

        UserDto found = userService.getById(created.getId());

        assertThat(found.getId()).isEqualTo(created.getId());
        assertThat(found.getEmail()).isEqualTo("ivan@test.ru");
    }

    @Test
    void getById_shouldThrowNotFoundForUnknownId() {
        assertThrows(NotFoundException.class, () -> userService.getById(999L));
    }

    @Test
    void getAll_shouldReturnAllUsers() {
        userService.create(userDto);
        userService.create(UserDto.builder().name("Второй").email("second@test.ru").build());

        Collection<UserDto> all = userService.getAll();

        assertThat(all).hasSize(2);
    }

    @Test
    void delete_shouldRemoveUser() {
        UserDto created = userService.create(userDto);

        userService.delete(created.getId());

        assertThrows(NotFoundException.class, () -> userService.getById(created.getId()));
    }

    @Test
    void delete_shouldThrowNotFoundWhenUserDoesNotExist() {
        assertThrows(NotFoundException.class, () -> userService.delete(999L));
    }
}