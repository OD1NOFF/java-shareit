package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BookingServiceImplTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User owner;
    private User booker;
    private Item item;

    @BeforeEach
    void setUp() {
        owner = userRepository.save(makeUser("Владелец", "owner@test.ru"));
        booker = userRepository.save(makeUser("Арендатор", "booker@test.ru"));

        Item newItem = new Item();
        newItem.setName("Дрель");
        newItem.setDescription("Мощная дрель");
        newItem.setAvailable(true);
        newItem.setOwner(owner);
        item = itemRepository.save(newItem);
    }

    private User makeUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return user;
    }

    private BookingRequestDto makeRequest(LocalDateTime start, LocalDateTime end) {
        return BookingRequestDto.builder()
                .itemId(item.getId())
                .start(start)
                .end(end)
                .build();
    }

    @Test
    void create_shouldCreateBookingInWaitingStatus() {
        BookingRequestDto requestDto = makeRequest(
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));

        BookingDto created = bookingService.create(booker.getId(), requestDto);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void create_shouldThrowValidationWhenItemUnavailable() {
        item.setAvailable(false);
        itemRepository.save(item);

        BookingRequestDto requestDto = makeRequest(
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));

        assertThrows(ValidationException.class, () -> bookingService.create(booker.getId(), requestDto));
    }

    @Test
    void create_shouldThrowNotFoundWhenOwnerBooksOwnItem() {
        BookingRequestDto requestDto = makeRequest(
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));

        assertThrows(NotFoundException.class, () -> bookingService.create(owner.getId(), requestDto));
    }

    @Test
    void create_shouldThrowNotFoundWhenItemDoesNotExist() {
        BookingRequestDto requestDto = BookingRequestDto.builder()
                .itemId(999L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        assertThrows(NotFoundException.class, () -> bookingService.create(booker.getId(), requestDto));
    }

    @Test
    void create_shouldThrowValidationWhenEndBeforeStart() {
        BookingRequestDto requestDto = makeRequest(
                LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(1));

        assertThrows(ValidationException.class, () -> bookingService.create(booker.getId(), requestDto));
    }

    @Test
    void approve_shouldSetApprovedStatus() {
        BookingDto created = bookingService.create(booker.getId(), makeRequest(
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)));

        BookingDto approved = bookingService.approve(owner.getId(), created.getId(), true);

        assertThat(approved.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void approve_shouldSetRejectedStatus() {
        BookingDto created = bookingService.create(booker.getId(), makeRequest(
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)));

        BookingDto rejected = bookingService.approve(owner.getId(), created.getId(), false);

        assertThat(rejected.getStatus()).isEqualTo(BookingStatus.REJECTED);
    }

    @Test
    void approve_shouldThrowForbiddenWhenNotOwner() {
        BookingDto created = bookingService.create(booker.getId(), makeRequest(
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)));

        assertThrows(ForbiddenException.class,
                () -> bookingService.approve(booker.getId(), created.getId(), true));
    }

    @Test
    void approve_shouldThrowValidationWhenAlreadyDecided() {
        BookingDto created = bookingService.create(booker.getId(), makeRequest(
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)));

        bookingService.approve(owner.getId(), created.getId(), true);

        assertThrows(ValidationException.class,
                () -> bookingService.approve(owner.getId(), created.getId(), false));
    }

    @Test
    void getById_shouldReturnBookingForBookerOrOwner() {
        BookingDto created = bookingService.create(booker.getId(), makeRequest(
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)));

        BookingDto foundByBooker = bookingService.getById(booker.getId(), created.getId());
        BookingDto foundByOwner = bookingService.getById(owner.getId(), created.getId());

        assertThat(foundByBooker.getId()).isEqualTo(created.getId());
        assertThat(foundByOwner.getId()).isEqualTo(created.getId());
    }

    @Test
    void getById_shouldThrowNotFoundForUnrelatedUser() {
        BookingDto created = bookingService.create(booker.getId(), makeRequest(
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)));

        User stranger = userRepository.save(makeUser("Посторонний", "stranger@test.ru"));

        assertThrows(NotFoundException.class, () -> bookingService.getById(stranger.getId(), created.getId()));
    }

    @Test
    void getAllByBooker_shouldReturnAllForAllState() {
        bookingService.create(booker.getId(), makeRequest(
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)));

        List<BookingDto> result = bookingService.getAllByBooker(booker.getId(), BookingState.ALL);

        assertThat(result).hasSize(1);
    }

    @Test
    void getAllByBooker_shouldReturnEmptyForWaitingWhenNoneWaiting() {
        BookingDto created = bookingService.create(booker.getId(), makeRequest(
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)));
        bookingService.approve(owner.getId(), created.getId(), true);

        List<BookingDto> waiting = bookingService.getAllByBooker(booker.getId(), BookingState.WAITING);

        assertThat(waiting).isEmpty();
    }

    @Test
    void getAllByBooker_shouldReturnRejected() {
        BookingDto created = bookingService.create(booker.getId(), makeRequest(
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)));
        bookingService.approve(owner.getId(), created.getId(), false);

        List<BookingDto> rejected = bookingService.getAllByBooker(booker.getId(), BookingState.REJECTED);

        assertThat(rejected).hasSize(1);
    }

    @Test
    void getAllByOwner_shouldReturnAllForOwner() {
        bookingService.create(booker.getId(), makeRequest(
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)));

        List<BookingDto> result = bookingService.getAllByOwner(owner.getId(), BookingState.ALL);

        assertThat(result).hasSize(1);
    }
}