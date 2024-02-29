package com.pawatask.auth.dto;

import com.pawatask.auth.controller.validation.EmailValidation;
import com.pawatask.auth.controller.validation.PasswordValidation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequestDto(
    @NotBlank(message = "{field.mandatory}")
    @Size(max = 30, message = "{maxLength.exceeded}")
    String userName,
    @NotBlank(message = "{field.mandatory}")
    @EmailValidation
    @Size(max = 200, message = "{maxLength.exceeded}")
    String email,
    @NotBlank(message = "{field.mandatory}")
    @PasswordValidation
    @Size(max = 30, message = "{maxLength.exceeded}")
    String password
) {}
