package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BookingEqualsTest {

    @Test
    void equals_shouldReturnTrueForSameId() {
        Booking booking1 = new Booking();
        booking1.setId(1L);

        Booking booking2 = new Booking();
        booking2.setId(1L);

        assertThat(booking1).isEqualTo(booking2);
    }

    @Test
    void equals_shouldReturnFalseForDifferentId() {
        Booking booking1 = new Booking();
        booking1.setId(1L);

        Booking booking2 = new Booking();
        booking2.setId(2L);

        assertThat(booking1).isNotEqualTo(booking2);
    }

    @Test
    void equals_shouldReturnFalseForNullId() {
        Booking booking1 = new Booking();
        booking1.setId(1L);

        Booking booking2 = new Booking();

        assertThat(booking1).isNotEqualTo(booking2);
    }

    @Test
    void equals_shouldReturnTrueForSameInstance() {
        Booking booking = new Booking();
        booking.setId(1L);

        assertThat(booking).isEqualTo(booking);
    }

    @Test
    void equals_shouldReturnFalseForDifferentClass() {
        Booking booking = new Booking();
        booking.setId(1L);

        assertThat(booking).isNotEqualTo("не бронирование");
    }

    @Test
    void hashCode_shouldBeConsistent() {
        Booking booking = new Booking();
        booking.setId(1L);

        assertThat(booking.hashCode()).isEqualTo(Booking.class.hashCode());
    }
}