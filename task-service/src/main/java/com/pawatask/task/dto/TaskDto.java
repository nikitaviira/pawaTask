package com.pawatask.task.dto;

import com.pawatask.task.domain.task.TaskPriority;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record TaskDto(
    @Nullable
    Long id,
    @NotBlank(message = "{field.mandatory}")
    @Size(max = 150, message = "{maxLength.exceeded}")
    String title,
    @Size(max = 1000, message = "{maxLength.exceeded}")
    String description,
    @NotNull(message = "{field.mandatory}")
    TaskPriority priority,
    @NotNull(message = "{field.mandatory}")
    LocalDate dueDate
) {}
