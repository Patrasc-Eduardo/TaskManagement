package com.rrom.task_management.controller;

import com.rrom.task_management.dto.request.TaskCreateDto;
import com.rrom.task_management.dto.request.TaskUpdateDto;
import com.rrom.task_management.dto.response.TaskResponseDto;
import com.rrom.task_management.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("api/v1/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<TaskResponseDto> createTask(@RequestHeader("X-User") String currentUser,
                                                      @RequestBody TaskCreateDto dto) {
        TaskResponseDto response = taskService.createTask(currentUser, dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public Page<TaskResponseDto> listTasks(@RequestHeader("X-User") String currentUser,
                                           Pageable pageable) {
        return taskService.listTasks(currentUser, pageable);
    }

    @GetMapping("/{taskId}")
    public TaskResponseDto getTaskById(@RequestHeader("X-User") String currentUser,
                                       @PathVariable Long taskId) {
        return taskService.getTaskById(currentUser, taskId);
    }

    @PutMapping("/{taskId}")
    public TaskResponseDto updateTask(@RequestHeader("X-User") String currentUser,
                                      @PathVariable Long taskId,
                                      @RequestBody TaskUpdateDto updateDTO) {
        return taskService.updateTask(currentUser, taskId, updateDTO);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@RequestHeader("X-User") String currentUser,
                                           @PathVariable Long taskId) {
        taskService.deleteTask(currentUser, taskId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{taskId}/share")
    public ResponseEntity<Void> shareTask(@RequestHeader("X-User") String currentUser,
                                          @PathVariable Long taskId,
                                          @RequestParam("user") String shareWithUsername) {
        taskService.shareTask(currentUser, taskId, shareWithUsername);
        return ResponseEntity.ok().build();
    }
}
