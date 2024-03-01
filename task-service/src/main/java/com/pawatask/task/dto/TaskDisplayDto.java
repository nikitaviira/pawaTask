package com.pawatask.task.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pawatask.task.domain.task.TaskPriority;

import java.time.LocalDate;

public record TaskDisplayDto(
    Long id,
    String title,
    TaskPriority priority,
    @JsonFormat(pattern = "dd.MM.yyyy")
    LocalDate dueDate
) {}
