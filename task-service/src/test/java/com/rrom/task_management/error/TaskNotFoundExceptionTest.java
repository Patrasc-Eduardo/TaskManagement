package com.rrom.task_management.error;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskNotFoundExceptionTest {

    @Test
    void constructor_SetsMessage() {
        TaskNotFoundException ex = new TaskNotFoundException("Not found msg");
        assertEquals("Not found msg", ex.getMessage());
    }
}
