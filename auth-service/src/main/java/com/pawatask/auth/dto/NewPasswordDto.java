package com.pawatask.auth.dto;

import com.pawatask.auth.controller.validation.PasswordValidation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NewPasswordDto(
    @NotBlank(message = "{field.mandatory}")
    @PasswordValidation
    @Size(max = 30, message = "{maxLength.exceeded}")
    String newPassword
) {}
