package com.rrom.task_management.controller;

import com.rrom.task_management.dto.request.TaskCreateDto;
import com.rrom.task_management.dto.request.TaskUpdateDto;
import com.rrom.task_management.dto.response.TaskResponseDto;
import com.rrom.task_management.service.TaskService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class TaskControllerTest {

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createTask_Returns201() {
        TaskCreateDto dto = new TaskCreateDto();
        dto.setTitle("Controller Created Task");

        TaskResponseDto mockResp = new TaskResponseDto();
        mockResp.setId(123L);
        mockResp.setTitle("Controller Created Task");

        when(taskService.createTask("ownerUser", dto)).thenReturn(mockResp);

        ResponseEntity<TaskResponseDto> response = taskController.createTask("ownerUser", dto);

        assertEquals(201, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(123L, response.getBody().getId());
        assertEquals("Controller Created Task", response.getBody().getTitle());
    }

    @Test
    void listTasks_ReturnsPage() {
        TaskResponseDto dto1 = new TaskResponseDto();
        dto1.setId(1L);
        dto1.setTitle("Task 1");

        Page<TaskResponseDto> mockPage = new PageImpl<>(List.of(dto1));

        Pageable pageable = PageRequest.of(0, 10);
        when(taskService.listTasks("ownerUser", pageable)).thenReturn(mockPage);

        Page<TaskResponseDto> result = taskController.listTasks("ownerUser", pageable);
        assertEquals(1, result.getTotalElements());
        assertEquals("Task 1", result.getContent().getFirst().getTitle());
    }

    @Test
    void getTaskById_Success() {
        TaskResponseDto dto = new TaskResponseDto();
        dto.setId(55L);
        dto.setTitle("GetTask Title");

        when(taskService.getTaskById("ownerUser", 55L)).thenReturn(dto);

        TaskResponseDto resp = taskController.getTaskById("ownerUser", 55L);
        assertEquals(55L, resp.getId());
        assertEquals("GetTask Title", resp.getTitle());
    }

    @Test
    void updateTask_Success() {
        TaskUpdateDto updateDto = new TaskUpdateDto();
        updateDto.setTitle("Updated Title");

        TaskResponseDto mockResp = new TaskResponseDto();
        mockResp.setId(100L);
        mockResp.setTitle("Updated Title");

        when(taskService.updateTask("ownerUser", 100L, updateDto)).thenReturn(mockResp);

        TaskResponseDto resp = taskController.updateTask("ownerUser", 100L, updateDto);
        assertEquals(100L, resp.getId());
        assertEquals("Updated Title", resp.getTitle());
    }

    @Test
    void deleteTask_Returns204() {
        // no return from service
        doNothing().when(taskService).deleteTask("ownerUser", 10L);

        ResponseEntity<Void> resp = taskController.deleteTask("ownerUser", 10L);
        assertEquals(204, resp.getStatusCodeValue());
        verify(taskService).deleteTask("ownerUser", 10L);
    }

    @Test
    void shareTask_ReturnsOk() {
        doNothing().when(taskService).shareTask("ownerUser", 22L, "otherUser");
        ResponseEntity<Void> resp = taskController.shareTask("ownerUser", 22L, "otherUser");
        assertEquals(200, resp.getStatusCodeValue());
        verify(taskService).shareTask("ownerUser", 22L, "otherUser");
    }
}
