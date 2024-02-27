package com.pawatask.task.dto;

import jakarta.annotation.Nullable;

public record CommentDto(
    String comment,
    @Nullable
    String createdBy,
    String createdAt
) {}
