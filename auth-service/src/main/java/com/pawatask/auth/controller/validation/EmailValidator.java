package com.pawatask.auth.controller.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

class EmailValidator implements ConstraintValidator<EmailValidation, String> {
    private final Pattern pattern = compile("^[\\w\\-.]+@([\\w\\-]+\\.)+[\\w\\-]{2,4}$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return pattern.matcher(value).matches();
    }
}