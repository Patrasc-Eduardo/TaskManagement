package com.rrom.task_management.service;

import com.rrom.task_management.dto.request.TaskCreateDto;
import com.rrom.task_management.dto.request.TaskUpdateDto;
import com.rrom.task_management.dto.response.TaskResponseDto;
import com.rrom.task_management.error.TaskNotFoundException;
import com.rrom.task_management.error.UnauthorizedException;
import com.rrom.task_management.model.Task;
import com.rrom.task_management.model.TaskStatus;
import com.rrom.task_management.model.User;
import com.rrom.task_management.repository.TaskRepository;
import com.rrom.task_management.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepo;

    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private TaskService taskService;

    private User userOwner;
    private User userOther;

    @BeforeEach
    void setup() {
        userOwner = new User();
        userOwner.setId(1L);
        userOwner.setUsername("ownerUser");

        userOther = new User();
        userOther.setId(2L);
        userOther.setUsername("otherUser");
    }

    @Test
    void createTask_SetsDefaultStatus_WhenNoStatusProvided() {
        // Mock user
        when(userRepo.findByUsername("ownerUser")).thenReturn(Optional.of(userOwner));

        TaskCreateDto dto = new TaskCreateDto();
        dto.setTitle("New Task");

        Task saved = new Task();
        saved.setId(100L);
        saved.setTitle("New Task");
        saved.setStatus(TaskStatus.TODO);
        saved.setOwner(userOwner);

        when(taskRepo.save(any(Task.class))).thenReturn(saved);

        TaskResponseDto response = taskService.createTask("ownerUser", dto);

        assertEquals(100L, response.getId());
        assertEquals("New Task", response.getTitle());
        assertEquals("TODO", response.getStatus());
        verify(taskRepo).save(any(Task.class));
    }

    @Test
    void createTask_ThrowsIfUserNotFound() {
        when(userRepo.findByUsername("missingUser")).thenReturn(Optional.empty());

        TaskCreateDto dto = new TaskCreateDto();
        dto.setTitle("Task Title");

        assertThrows(RuntimeException.class, () -> taskService.createTask("missingUser", dto));
        verify(taskRepo, never()).save(any(Task.class));
    }

    @Test
    void listTasks_ReturnsPage() {
        when(userRepo.findByUsername("ownerUser")).thenReturn(Optional.of(userOwner));

        Task t1 = new Task();
        t1.setId(1L);
        t1.setTitle("Owner Task");
        Page<Task> mockPage = new PageImpl<>(List.of(t1));

        Pageable pageable = PageRequest.of(0, 10);
        when(taskRepo.findAllAccessibleByUser(userOwner, pageable)).thenReturn(mockPage);

        Page<TaskResponseDto> result = taskService.listTasks("ownerUser", pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Owner Task", result.getContent().get(0).getTitle());
    }

    @Test
    void getTaskById_Found() {
        when(userRepo.findByUsername("ownerUser")).thenReturn(Optional.of(userOwner));

        Task t = new Task();
        t.setId(55L);
        t.setTitle("Accessible Task");
        when(taskRepo.findByIdAndUserAccessible(55L, userOwner))
                .thenReturn(Optional.of(t));

        TaskResponseDto resp = taskService.getTaskById("ownerUser", 55L);
        assertEquals(55L, resp.getId());
        assertEquals("Accessible Task", resp.getTitle());
    }

    @Test
    void getTaskById_NotFound_ThrowsTaskNotFound() {
        when(userRepo.findByUsername("ownerUser")).thenReturn(Optional.of(userOwner));
        when(taskRepo.findByIdAndUserAccessible(999L, userOwner))
                .thenReturn(Optional.empty());

        assertThrows(
                TaskNotFoundException.class,
                () -> taskService.getTaskById("ownerUser", 999L)
        );
    }

    @Test
    void updateTask_TaskNotFound_Throws() {
        when(taskRepo.findById(999L)).thenReturn(Optional.empty());
        assertThrows(TaskNotFoundException.class,
                () -> taskService.updateTask("ownerUser", 999L, new TaskUpdateDto()));
    }

    @Test
    void updateTask_Unauthorized_Throws() {
        Task t = new Task();
        t.setId(100L);
        t.setOwner(userOther); // actual owner is otherUser

        when(taskRepo.findById(100L)).thenReturn(Optional.of(t));

        TaskUpdateDto dto = new TaskUpdateDto();
        dto.setTitle("New Title");

        assertThrows(
                UnauthorizedException.class,
                () -> taskService.updateTask("ownerUser", 100L, dto)
        );
        verify(taskRepo, never()).save(any(Task.class));
    }

    @Test
    void updateTask_MergesFields() {
        Task t = new Task();
        t.setId(100L);
        t.setOwner(userOwner);
        t.setTitle("Old Title");
        t.setStatus(TaskStatus.TODO);

        when(taskRepo.findById(100L)).thenReturn(Optional.of(t));
        when(taskRepo.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        TaskUpdateDto dto = new TaskUpdateDto();
        dto.setTitle("Updated Title");
        dto.setStatus("IN_PROGRESS");

        var result = taskService.updateTask("ownerUser", 100L, dto);
        assertEquals("Updated Title", result.getTitle());
        assertEquals("IN_PROGRESS", result.getStatus());
        verify(taskRepo).save(t);
    }

    @Test
    void deleteTask_Unauthorized() {
        Task t = new Task();
        t.setId(10L);
        t.setOwner(userOther);

        when(taskRepo.findById(10L)).thenReturn(Optional.of(t));
        when(userRepo.findByUsername("ownerUser")).thenReturn(Optional.of(userOwner));

        assertThrows(
                UnauthorizedException.class,
                () -> taskService.deleteTask("ownerUser", 10L)
        );
        verify(taskRepo, never()).delete(any(Task.class));
    }

    @Test
    void deleteTask_Succeeds() {
        Task t = new Task();
        t.setId(10L);
        t.setOwner(userOwner);

        when(taskRepo.findById(10L)).thenReturn(Optional.of(t));
        when(userRepo.findByUsername("ownerUser")).thenReturn(Optional.of(userOwner));

        taskService.deleteTask("ownerUser", 10L);
        verify(taskRepo).delete(t);
    }

    @Test
    void shareTask_OnlyOwnerCanShare() {
        Task t = new Task();
        t.setId(22L);
        t.setOwner(userOther);

        when(taskRepo.findById(22L)).thenReturn(Optional.of(t));
        when(userRepo.findByUsername("ownerUser")).thenReturn(Optional.of(userOwner));

        assertThrows(
                UnauthorizedException.class,
                () -> taskService.shareTask("ownerUser", 22L, "someOther")
        );
        verify(taskRepo, never()).save(any(Task.class));
    }

    @Test
    void shareTask_UserNotFound_Throws() {
        Task t = new Task();
        t.setId(22L);
        t.setOwner(userOwner);

        when(taskRepo.findById(22L)).thenReturn(Optional.of(t));
        when(userRepo.findByUsername("ownerUser")).thenReturn(Optional.of(userOwner));
        when(userRepo.findByUsername("missingUser")).thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> taskService.shareTask("ownerUser", 22L, "missingUser")
        );
        verify(taskRepo, never()).save(any(Task.class));
    }

    @Test
    void shareTask_Success() {
        Task t = new Task();
        t.setId(22L);
        t.setOwner(userOwner);

        when(taskRepo.findById(22L)).thenReturn(Optional.of(t));
        when(userRepo.findByUsername("ownerUser")).thenReturn(Optional.of(userOwner));
        when(userRepo.findByUsername("otherUser")).thenReturn(Optional.of(userOther));

        when(taskRepo.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        taskService.shareTask("ownerUser", 22L, "otherUser");
        assertTrue(t.getSharedWithUsers().contains(userOther));
        verify(taskRepo).save(t);
    }
}
