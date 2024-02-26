package com.pawatask.auth.dto;

public record CredentialsDto(
    String jwtToken,
    String email,
    String userName
) {}
