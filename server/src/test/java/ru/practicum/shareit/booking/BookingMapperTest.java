package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class BookingMapperTest {

    @Test
    void toBookingDto_shouldMapAllFields() {
        User owner = new User();
        owner.setId(1L);
        Item item = new Item();
        item.setId(1L);
        item.setName("Дрель");
        item.setOwner(owner);

        User booker = new User();
        booker.setId(2L);
        booker.setName("Арендатор");

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.of(2026, 8, 1, 10, 0));
        booking.setEnd(LocalDateTime.of(2026, 8, 2, 10, 0));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        BookingDto dto = BookingMapper.toBookingDto(booking);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getStatus()).isEqualTo(BookingStatus.WAITING);
        assertThat(dto.getItem().getName()).isEqualTo("Дрель");
        assertThat(dto.getBooker().getId()).isEqualTo(2L);
    }

    @Test
    void toBooking_shouldSetWaitingStatus() {
        Item item = new Item();
        item.setId(1L);
        User booker = new User();
        booker.setId(2L);

        BookingRequestDto requestDto = BookingRequestDto.builder()
                .itemId(1L)
                .start(LocalDateTime.of(2026, 8, 1, 10, 0))
                .end(LocalDateTime.of(2026, 8, 2, 10, 0))
                .build();

        Booking booking = BookingMapper.toBooking(requestDto, item, booker);

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.WAITING);
        assertThat(booking.getItem()).isEqualTo(item);
        assertThat(booking.getBooker()).isEqualTo(booker);
    }

    @Test
    void toBookingShortDto_shouldReturnNullForNullBooking() {
        BookingShortDto result = BookingMapper.toBookingShortDto(null);

        assertThat(result).isNull();
    }

    @Test
    void toBookingShortDto_shouldMapIdAndBookerId() {
        User booker = new User();
        booker.setId(2L);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBooker(booker);

        BookingShortDto dto = BookingMapper.toBookingShortDto(booking);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getBookerId()).isEqualTo(2L);
    }
}