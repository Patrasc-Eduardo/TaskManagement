package com.rrom.task_management.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(TaskNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public Map<String, Object> handleTaskNotFound(TaskNotFoundException ex) {
        log.warn("Task not found error: {}", ex.getMessage());
        return Map.of(
                "timestamp", LocalDateTime.now(),
                "error", "Task not found",
                "message", ex.getMessage()
        );
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public Map<String, Object> handleUnauthorized(UnauthorizedException ex) {
        log.warn("Unauthorized error: {}", ex.getMessage());
        return Map.of(
                "timestamp", LocalDateTime.now(),
                "error", "Forbidden",
                "message", ex.getMessage()
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public Map<String, Object> handleGeneralException(Exception ex) {
        log.error("Unhandled error: {}", ex.getMessage(), ex);
        return Map.of(
                "timestamp", LocalDateTime.now(),
                "error", "Internal Server Error",
                "message", ex.getMessage()
        );
    }
}
