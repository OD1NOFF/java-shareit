package ru.practicum.shareit.booking.dto;

public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static BookingState from(String value) {
        for (BookingState state : values()) {
            if (state.name().equalsIgnoreCase(value)) {
                return state;
            }
        }
        return null;
    }
}