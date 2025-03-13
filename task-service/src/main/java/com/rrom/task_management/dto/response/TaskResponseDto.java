package com.rrom.task_management.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskResponseDto {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private String status;
    private String ownerUsername;
}

