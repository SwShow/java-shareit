package ru.practicum.shareit.shareit.exception;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ErrorHandlerTest {

    private final ErrorHandler errorHandler;

    @Test
    void runtimeExceptionTest() {
        ErrorResponse error = errorHandler.handleBadRequestException(new BadRequestException("message"));
        Assertions.assertEquals(error.getError(), "message");
    }

    @Test
    void badRequestException() {
        ErrorResponse error = errorHandler.handleNotFoundException(new NotFoundException("message"));
        Assertions.assertEquals(error.getError(), "message");
    }
}