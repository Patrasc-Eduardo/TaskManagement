package com.rrom.task_management.dto;

import com.rrom.task_management.dto.response.TaskResponseDto;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaskResponseDtoTest {

    @Test
    public void testSettersAndGetters() {
        LocalDateTime now = LocalDateTime.now();
        TaskResponseDto taskResponseDto = new TaskResponseDto();
        taskResponseDto.setDescription("test desc");
        taskResponseDto.setId(1L);
        taskResponseDto.setTitle("test title");
        taskResponseDto.setDueDate(now);
        taskResponseDto.setStatus("DONE");
        taskResponseDto.setOwnerUsername("username");

        assertEquals("test title", taskResponseDto.getTitle());
        assertEquals(1L, taskResponseDto.getId());
        assertEquals("test desc", taskResponseDto.getDescription());
        assertEquals("DONE", taskResponseDto.getStatus());
        assertEquals(now, taskResponseDto.getDueDate());
        assertEquals("username", taskResponseDto.getOwnerUsername());
    }

}
