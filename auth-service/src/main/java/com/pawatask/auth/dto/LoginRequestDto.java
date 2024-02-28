package com.pawatask.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto(
    @NotBlank(message = "{field.mandatory}")
    String email,
    @NotBlank(message = "{field.mandatory}")
    String password
) {}
