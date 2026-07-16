package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ItemRequestServiceImplTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private UserRepository userRepository;

    private User requestor;
    private User otherUser;

    @BeforeEach
    void setUp() {
        requestor = userRepository.save(makeUser("Запросчик", "requestor@test.ru"));
        otherUser = userRepository.save(makeUser("Другой", "other@test.ru"));
    }

    private User makeUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return user;
    }

    @Test
    void create_shouldSaveRequest() {
        ItemRequestDto dto = ItemRequestDto.builder().description("Нужна дрель").build();

        ItemRequestResponseDto created = itemRequestService.create(requestor.getId(), dto);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getDescription()).isEqualTo("Нужна дрель");
        assertThat(created.getItems()).isEmpty();
    }

    @Test
    void create_shouldThrowNotFoundForUnknownUser() {
        ItemRequestDto dto = ItemRequestDto.builder().description("Нужна дрель").build();

        assertThrows(NotFoundException.class, () -> itemRequestService.create(999L, dto));
    }

    @Test
    void getOwnRequests_shouldReturnOnlyOwnRequestsSortedByDate() {
        itemRequestService.create(requestor.getId(), ItemRequestDto.builder().description("Первый").build());
        itemRequestService.create(requestor.getId(), ItemRequestDto.builder().description("Второй").build());
        itemRequestService.create(otherUser.getId(), ItemRequestDto.builder().description("Чужой").build());

        List<ItemRequestResponseDto> own = itemRequestService.getOwnRequests(requestor.getId());

        assertThat(own).hasSize(2);
    }

    @Test
    void getAllRequests_shouldExcludeOwnRequests() {
        itemRequestService.create(requestor.getId(), ItemRequestDto.builder().description("Свой").build());
        itemRequestService.create(otherUser.getId(), ItemRequestDto.builder().description("Чужой").build());

        List<ItemRequestResponseDto> allForRequestor = itemRequestService.getAllRequests(requestor.getId());

        assertThat(allForRequestor).hasSize(1);
        assertThat(allForRequestor.get(0).getDescription()).isEqualTo("Чужой");
    }

    @Test
    void getById_shouldReturnRequestWithAnswers() {
        ItemRequestResponseDto created = itemRequestService.create(
                requestor.getId(), ItemRequestDto.builder().description("Нужна дрель").build());

        ItemRequestResponseDto found = itemRequestService.getById(otherUser.getId(), created.getId());

        assertThat(found.getId()).isEqualTo(created.getId());
    }

    @Test
    void getById_shouldThrowNotFoundForUnknownRequest() {
        assertThrows(NotFoundException.class, () -> itemRequestService.getById(requestor.getId(), 999L));
    }
}