package com.pawatask.auth.dto;

public record RegisterRequestDto(
    String email,
    String userName,
    String password
) {}
