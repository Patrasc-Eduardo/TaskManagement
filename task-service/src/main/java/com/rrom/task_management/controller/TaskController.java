package com.rrom.task_management.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/v1/tasks")
public class TaskController {

    @GetMapping("/{taskId}")
    public ResponseEntity<String> getTask(@PathVariable Long taskId) {
        return  ResponseEntity.ok("Task id: " + taskId);
    }

    @PostMapping
    public ResponseEntity<String> createTask(@RequestBody String task) {
        // Logic to create a task (mock response for this example)
        return ResponseEntity.status(HttpStatus.CREATED).body("Task created: " + task);
    }
}
