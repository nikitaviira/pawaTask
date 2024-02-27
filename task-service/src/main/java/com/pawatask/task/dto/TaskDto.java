package com.pawatask.task.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.pawatask.task.domain.task.TaskPriority;
import com.pawatask.task.util.LocalDateSerializer;
import jakarta.annotation.Nullable;

import java.time.LocalDate;

public record TaskDto(
    @Nullable
    Long id,
    String title,
    String description,
    TaskPriority priority,
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonSerialize(using = LocalDateSerializer.class)
    LocalDate dueDate
) {}
