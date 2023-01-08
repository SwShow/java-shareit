package ru.practicum.shareit.exception;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.SQLException;
import java.util.NoSuchElementException;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ErrorHandlerTest {

    private final ErrorHandler errorHandler;

    @Test
    void validationExceptionTest() {
        ErrorResponse error = errorHandler.handleException(new ValidationException("message"));
        Assertions.assertEquals(error.getError(), "message");
    }

    @Test
    void conflictExceptionTest() {
        ErrorResponse error = errorHandler.handleException(new ConflictException("message"));
        Assertions.assertEquals(error.getError(), "message");
    }

    @Test
    void nullPointerExceptionTest() {
        ErrorResponse error = errorHandler.handleException(new NullPointerException("message"));
        Assertions.assertEquals(error.getError(), "message");
    }

    @Test
    void runtimeExceptionTest() {
        ErrorResponse error = errorHandler.handleException(new RuntimeException("message"));
        Assertions.assertEquals(error.getError(), "message");
    }

    @Test
    void noSuchElementExceptionTest() {
        ErrorResponse error = errorHandler.handleException(new NoSuchElementException("message"));
        Assertions.assertEquals(error.getError(), "message");
    }

    @Test
    void sQLExceptionTest() {
        ErrorResponse error = errorHandler.handleException(new SQLException("message"));
        Assertions.assertEquals(error.getError(), "message");
    }

    @Test
    void badRequestException() {
        ErrorResponse error = errorHandler.handleException(new BadRequestException("message"));
        Assertions.assertEquals(error.getError(), "message");
    }
}