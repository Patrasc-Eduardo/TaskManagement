package com.rrom.task_management.error;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UnauthorizedExceptionTest {

    @Test
    void constructor_SetsMessage() {
        UnauthorizedException ex = new UnauthorizedException("Forbidden action");
        assertEquals("Forbidden action", ex.getMessage());
    }
}
