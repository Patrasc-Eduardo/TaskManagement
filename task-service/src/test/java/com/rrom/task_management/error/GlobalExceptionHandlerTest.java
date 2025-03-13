package com.rrom.task_management.error;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GlobalExceptionHandlerTest {
    private GlobalExceptionHandler handler;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleTaskNotFound_Returns404Fields() {
        TaskNotFoundException ex = new TaskNotFoundException("Task with ID=99 not found");
        Map<String, Object> response = handler.handleTaskNotFound(ex);

        assertNotNull(response.get("timestamp"));
        assertEquals("Task not found", response.get("error"));
        assertEquals("Task with ID=99 not found", response.get("message"));
    }

    @Test
    void handleUnauthorized_Returns403Fields() {
        UnauthorizedException ex = new UnauthorizedException("You are not allowed");
        Map<String, Object> response = handler.handleUnauthorized(ex);

        assertNotNull(response.get("timestamp"));
        assertEquals("Forbidden", response.get("error"));
        assertEquals("You are not allowed", response.get("message"));
    }

    @Test
    void handleGeneralException_Returns500Fields() {
        Exception ex = new Exception("Something went wrong");
        Map<String, Object> response = handler.handleGeneralException(ex);

        assertNotNull(response.get("timestamp"));
        assertEquals("Internal Server Error", response.get("error"));
        assertEquals("Something went wrong", response.get("message"));
    }
}
