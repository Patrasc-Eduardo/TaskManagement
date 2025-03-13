package com.rrom.task_management.dto.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskUpdateDto {
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private String status;
}
