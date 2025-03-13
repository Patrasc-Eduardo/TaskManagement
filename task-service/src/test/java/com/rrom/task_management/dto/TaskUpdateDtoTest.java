package com.rrom.task_management.dto;

import com.rrom.task_management.dto.request.TaskUpdateDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskUpdateDtoTest {

    @Test
    void testSettersAndGetters() {
        TaskUpdateDto dto = new TaskUpdateDto();
        dto.setTitle("Update Title");
        dto.setDescription("Update Desc");
        dto.setStatus("Update Status");
        assertEquals("Update Title", dto.getTitle());
        assertEquals("Update Desc", dto.getDescription());
        assertEquals("Update Status", dto.getStatus());
    }
}
