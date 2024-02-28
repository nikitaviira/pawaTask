package com.pawatask.task.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.pawatask.task.domain.task.TaskPriority;
import com.pawatask.task.util.LocalDateSerializer;
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
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonSerialize(using = LocalDateSerializer.class)
    LocalDate dueDate
) {}
