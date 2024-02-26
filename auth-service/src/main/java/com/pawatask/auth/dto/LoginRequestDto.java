package com.pawatask.auth.dto;

public record LoginRequestDto(
    String email,
    String password
) {}
