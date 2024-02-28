package com.pawatask.task.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SaveCommentDto(
    @NotNull(message = "{field.mandatory}")
    Long taskId,
    @NotBlank(message = "{field.mandatory}")
    @Size(max = 150, message = "{maxLength.exceeded}")
    String comment
) {}
