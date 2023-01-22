package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;



class ConflictExceptionTest {

    @Test
    void setConflictException() {
        ConflictException conflictException = new ConflictException("message");
    }

}