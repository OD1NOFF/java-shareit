package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorHandlerTest {

    private final ErrorHandler errorHandler = new ErrorHandler();

    @Test
    void handleNotFound_shouldReturnErrorResponse() {
        NotFoundException exception = new NotFoundException("Не найдено");

        ErrorResponse response = errorHandler.handleNotFound(exception);

        assertThat(response.getError()).isEqualTo("Объект не найден");
        assertThat(response.getDescription()).isEqualTo("Не найдено");
    }

    @Test
    void handleForbidden_shouldReturnErrorResponse() {
        ForbiddenException exception = new ForbiddenException("Доступ запрещён");

        ErrorResponse response = errorHandler.handleForbidden(exception);

        assertThat(response.getError()).isEqualTo("Доступ запрещён");
        assertThat(response.getDescription()).isEqualTo("Доступ запрещён");
    }

    @Test
    void handleConflict_shouldReturnErrorResponse() {
        ConflictException exception = new ConflictException("Конфликт");

        ErrorResponse response = errorHandler.handleConflict(exception);

        assertThat(response.getError()).isEqualTo("Конфликт данных");
        assertThat(response.getDescription()).isEqualTo("Конфликт");
    }

    @Test
    void handleValidationException_shouldReturnErrorResponse() {
        ValidationException exception = new ValidationException("Ошибка");

        ErrorResponse response = errorHandler.handleValidationException(exception);

        assertThat(response.getError()).isEqualTo("Ошибка валидации");
        assertThat(response.getDescription()).isEqualTo("Ошибка");
    }

    @Test
    void handleOther_shouldReturnErrorResponse() {
        Throwable exception = new RuntimeException("Непредвиденная ошибка");

        ErrorResponse response = errorHandler.handleOther(exception);

        assertThat(response.getError()).isEqualTo("Непредвиденная ошибка");
        assertThat(response.getDescription()).isEqualTo("Непредвиденная ошибка");
    }
}