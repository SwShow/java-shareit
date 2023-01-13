package ru.practicum.shareit.shareit.exception;

import org.junit.jupiter.api.Test;

class BadRequestExceptionTest {

    @Test
    void setBadRequest() {
        BadRequestException badRequestException = new BadRequestException("message");
    }
}