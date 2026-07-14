package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(USER_ID_HEADER) long userId,
                                         @Valid @RequestBody BookItemRequestDto requestDto) {
        return bookingClient.create(userId, requestDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(@RequestHeader(USER_ID_HEADER) long ownerId,
                                          @PathVariable long bookingId,
                                          @RequestParam boolean approved) {
        return bookingClient.approve(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getById(@RequestHeader(USER_ID_HEADER) long userId,
                                          @PathVariable long bookingId) {
        return bookingClient.getById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByBooker(@RequestHeader(USER_ID_HEADER) long userId,
                                                 @RequestParam(defaultValue = "ALL") String state) {
        BookingState bookingState = BookingState.from(state);
        if (bookingState == null) {
            throw new IllegalArgumentException("Unknown state: " + state);
        }
        return bookingClient.getAllByBooker(userId, bookingState);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllByOwner(@RequestHeader(USER_ID_HEADER) long ownerId,
                                                @RequestParam(defaultValue = "ALL") String state) {
        BookingState bookingState = BookingState.from(state);
        if (bookingState == null) {
            throw new IllegalArgumentException("Unknown state: " + state);
        }
        return bookingClient.getAllByOwner(ownerId, bookingState);
    }
}