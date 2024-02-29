package com.pawatask.task.dto;

import com.pawatask.task.domain.task.TaskPriority;

import java.util.List;

public record TaskDetailsDto(
    Long id,
    String title,
    String description,
    String createdAt,
    String createdBy,
    String lastEditedBy,
    String dueDate,
    TaskPriority priority,
    List<CommentDto> comments
) {}
