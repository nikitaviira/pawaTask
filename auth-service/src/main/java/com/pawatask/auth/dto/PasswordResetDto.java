package com.pawatask.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record PasswordResetDto(
    @NotBlank(message = "{field.mandatory}")
    String email
) {}
