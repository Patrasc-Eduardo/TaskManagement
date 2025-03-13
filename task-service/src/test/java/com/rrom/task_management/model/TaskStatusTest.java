package com.rrom.task_management.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TaskStatusTest {
    @Test
    void testEnumValues() {
        TaskStatus[] statuses = TaskStatus.values();
        assertEquals(3, statuses.length);
        assertTrue(TaskStatus.valueOf("TODO") instanceof TaskStatus);
        assertTrue(TaskStatus.valueOf("IN_PROGRESS") instanceof TaskStatus);
        assertTrue(TaskStatus.valueOf("DONE") instanceof TaskStatus);
    }
}
