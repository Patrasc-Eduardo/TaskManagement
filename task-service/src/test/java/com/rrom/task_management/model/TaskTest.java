package com.rrom.task_management.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaskTest {
    @Test
    void testSettersAndGetters() {
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Test Title");
        task.setDescription("Some description");
        LocalDateTime now = LocalDateTime.now();
        task.setDueDate(now);
        task.setStatus(TaskStatus.IN_PROGRESS);

        assertEquals(1L, task.getId());
        assertEquals("Test Title", task.getTitle());
        assertEquals("Some description", task.getDescription());
        assertEquals(now, task.getDueDate());
        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());
    }
}
