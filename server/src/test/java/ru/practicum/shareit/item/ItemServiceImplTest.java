package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ItemServiceImplTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User owner;
    private User booker;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        owner = userRepository.save(makeUser("Владелец", "owner@test.ru"));
        booker = userRepository.save(makeUser("Арендатор", "booker@test.ru"));

        itemDto = ItemDto.builder()
                .name("Дрель")
                .description("Мощная дрель")
                .available(true)
                .build();
    }

    private User makeUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return user;
    }

    @Test
    void create_shouldSaveItemWithOwner() {
        ItemDto created = itemService.create(owner.getId(), itemDto);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("Дрель");
        assertThat(created.getAvailable()).isTrue();
    }

    @Test
    void create_shouldThrowNotFoundWhenOwnerDoesNotExist() {
        assertThrows(NotFoundException.class, () -> itemService.create(999L, itemDto));
    }

    @Test
    void update_shouldUpdateFieldsWhenOwnerMatches() {
        ItemDto created = itemService.create(owner.getId(), itemDto);

        ItemDto updateDto = ItemDto.builder()
                .name("Дрель Bosch")
                .available(false)
                .build();

        ItemDto updated = itemService.update(owner.getId(), created.getId(), updateDto);

        assertThat(updated.getName()).isEqualTo("Дрель Bosch");
        assertThat(updated.getAvailable()).isFalse();
        assertThat(updated.getDescription()).isEqualTo("Мощная дрель");
    }

    @Test
    void update_shouldThrowForbiddenWhenNotOwner() {
        ItemDto created = itemService.create(owner.getId(), itemDto);

        ItemDto updateDto = ItemDto.builder().name("Чужое имя").build();

        assertThrows(ForbiddenException.class,
                () -> itemService.update(booker.getId(), created.getId(), updateDto));
    }

    @Test
    void update_shouldThrowNotFoundWhenItemDoesNotExist() {
        ItemDto updateDto = ItemDto.builder().name("Имя").build();

        assertThrows(NotFoundException.class,
                () -> itemService.update(owner.getId(), 999L, updateDto));
    }

    @Test
    void getById_shouldReturnItemWithCommentsForAnyUser() {
        ItemDto created = itemService.create(owner.getId(), itemDto);

        ItemDto found = itemService.getById(created.getId(), booker.getId());

        assertThat(found.getId()).isEqualTo(created.getId());
        assertThat(found.getComments()).isEmpty();
    }

    @Test
    void getById_shouldIncludeBookingDatesForOwner() {
        ItemDto created = itemService.create(owner.getId(), itemDto);
        Item savedItem = itemRepository.findById(created.getId()).orElseThrow();

        Booking pastBooking = new Booking();
        pastBooking.setStart(LocalDateTime.now().minusDays(5));
        pastBooking.setEnd(LocalDateTime.now().minusDays(3));
        pastBooking.setStatus(BookingStatus.APPROVED);
        pastBooking.setBooker(booker);
        pastBooking.setItem(savedItem);
        bookingRepository.save(pastBooking);

        ItemDto found = itemService.getById(created.getId(), owner.getId());

        assertThat(found.getLastBooking()).isNotNull();
    }

    @Test
    void getById_shouldThrowNotFoundForUnknownItem() {
        assertThrows(NotFoundException.class, () -> itemService.getById(999L, owner.getId()));
    }

    @Test
    void getAllByOwner_shouldReturnOwnersItemsOnly() {
        itemService.create(owner.getId(), itemDto);
        itemService.create(owner.getId(), ItemDto.builder()
                .name("Отвёртка")
                .description("Крестовая")
                .available(true)
                .build());

        Collection<ItemDto> result = itemService.getAllByOwner(owner.getId());

        assertThat(result).hasSize(2);
    }

    @Test
    void search_shouldReturnOnlyAvailableMatchingItems() {
        itemService.create(owner.getId(), itemDto);
        itemService.create(owner.getId(), ItemDto.builder()
                .name("Дрель недоступная")
                .description("Сломана")
                .available(false)
                .build());

        Collection<ItemDto> result = itemService.search("дрель");

        assertThat(result).hasSize(1);
        assertThat(result.iterator().next().getName()).isEqualTo("Дрель");
    }

    @Test
    void search_shouldReturnEmptyForBlankText() {
        Collection<ItemDto> result = itemService.search("");

        assertThat(result).isEmpty();
    }

    @Test
    void addComment_shouldThrowValidationWhenUserNeverBooked() {
        ItemDto created = itemService.create(owner.getId(), itemDto);

        CommentDto commentDto = CommentDto.builder().text("Отличная вещь!").build();

        assertThrows(ValidationException.class,
                () -> itemService.addComment(booker.getId(), created.getId(), commentDto));
    }
}