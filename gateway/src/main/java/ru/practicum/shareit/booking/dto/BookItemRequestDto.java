package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookItemRequestDto {

    @NotNull(message = "Не указана вещь для бронирования")
    private Long itemId;

    @NotNull(message = "Дата начала бронирования обязательна")
    @FutureOrPresent(message = "Дата начала не может быть в прошлом")
    private LocalDateTime start;

    @NotNull(message = "Дата окончания бронирования обязательна")
    private LocalDateTime end;

    @AssertTrue(message = "Дата окончания должна быть позже даты начала")
    private boolean isEndAfterStart() {
        return start != null && end != null && end.isAfter(start);
    }
}