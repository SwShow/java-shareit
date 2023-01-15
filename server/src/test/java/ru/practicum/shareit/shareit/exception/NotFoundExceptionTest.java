package ru.practicum.shareit.shareit.exception;

import org.junit.jupiter.api.Test;

class NotFoundExceptionTest {

    @Test
    void setNotFoundException() {
        NotFoundException notFoundException = new NotFoundException("message");
    }
}
