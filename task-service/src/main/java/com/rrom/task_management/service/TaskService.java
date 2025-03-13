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
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
public class TaskService {

    private final TaskRepository taskRepo;
    private final UserRepository userRepo;

    public TaskService(TaskRepository taskRepo, UserRepository userRepo) {
        this.taskRepo = taskRepo;
        this.userRepo = userRepo;
    }

    public TaskResponseDto createTask(String currentUsername, TaskCreateDto dto) {
        User owner = getUserByUsername(currentUsername);

        Task entity = new Task();
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setDueDate(dto.getDueDate());
        if (dto.getStatus() != null) {
            entity.setStatus(TaskStatus.valueOf(dto.getStatus()));
        } else {
            entity.setStatus(TaskStatus.TODO);
        }
        entity.setOwner(owner);

        Task saved = taskRepo.save(entity);
        log.info("Task created with ID={}", saved.getId());
        return mapToResponseDTO(saved);
    }

    @Cacheable(value = "tasks", key = "#currentUsername + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<TaskResponseDto> listTasks(String currentUsername, Pageable pageable) {
        User user = getUserByUsername(currentUsername);
        Page<Task> page = taskRepo.findAllAccessibleByUser(user, pageable);
        return page.map(this::mapToResponseDTO);
    }

    @Cacheable(value = "tasks", key = "#currentUsername + '-' + #taskId")
    public TaskResponseDto getTaskById(String currentUsername, Long taskId) {
        User user = getUserByUsername(currentUsername);
        Task task = taskRepo.findByIdAndUserAccessible(taskId, user)
                .orElseThrow(() -> new TaskNotFoundException("Task not found or not accessible"));
        return mapToResponseDTO(task);
    }

    @CacheEvict(value = "tasks", allEntries = true)
    public TaskResponseDto updateTask(String currentUsername, Long taskId, TaskUpdateDto updateDTO) {

        Task task = taskRepo.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));

        if (!task.getOwner().getUsername().equals(currentUsername)) {
            throw new UnauthorizedException("You are not the owner of this task");
        }

        //TODO replace with mapstruct
        if (updateDTO.getTitle() != null) {
            task.setTitle(updateDTO.getTitle());
        }
        if (updateDTO.getDescription() != null) {
            task.setDescription(updateDTO.getDescription());
        }
        if (updateDTO.getDueDate() != null) {
            task.setDueDate(updateDTO.getDueDate());
        }
        if (updateDTO.getStatus() != null) {
            task.setStatus(TaskStatus.valueOf(updateDTO.getStatus()));
        }

        Task updated = taskRepo.save(task);
        log.info("Task updated with ID={}", updated.getId());
        return mapToResponseDTO(updated);
    }

    @CacheEvict(value = "tasks", allEntries = true)
    public void deleteTask(String currentUsername, Long taskId) {
        User user = getUserByUsername(currentUsername);
        Task task = taskRepo.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));

        if (!task.getOwner().equals(user)) {
            throw new UnauthorizedException("You are not the owner of this task");
        }

        taskRepo.delete(task);
        log.info("Task deleted with ID={}", taskId);
    }

    @CacheEvict(value = "tasks", allEntries = true)
    public void shareTask(String currentUsername, Long taskId, String shareWithUsername) {
        User owner = getUserByUsername(currentUsername);
        Task task = taskRepo.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));

        if (!task.getOwner().equals(owner)) {
            throw new UnauthorizedException("Only the owner can share this task");
        }

        User otherUser = userRepo.findByUsername(shareWithUsername)
                .orElseThrow(() -> new RuntimeException("User to share with not found: " + shareWithUsername));

        Set<User> sharedWith = task.getSharedWithUsers();
        sharedWith.add(otherUser);
        task.setSharedWithUsers(sharedWith);

        taskRepo.save(task);
        log.info("Task with ID={} shared with user={}", taskId, shareWithUsername);
    }

    private User getUserByUsername(String username) {
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    private TaskResponseDto mapToResponseDTO(Task entity) {
        TaskResponseDto dto = new TaskResponseDto();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setDueDate(entity.getDueDate());
        if (entity.getStatus() != null) {
            dto.setStatus(entity.getStatus().name());
        }
        if (entity.getOwner() != null) {
            dto.setOwnerUsername(entity.getOwner().getUsername());
        }
        return dto;
    }
}
