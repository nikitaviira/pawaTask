package com.pawatask.task.dto;

public record CommentDto(
    String comment,
    String createdBy,
    String createdAt
) {}
