package com.pawatask.auth.controller;

import com.pawatask.auth.dto.CredentialsDto;
import com.pawatask.auth.dto.LoginRequestDto;
import com.pawatask.auth.dto.RegisterRequestDto;
import com.pawatask.auth.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController extends BaseController {
  private final UserService userService;

  @PostMapping("login")
  public CredentialsDto login(@RequestBody LoginRequestDto loginRequestDto) {
    return userService.login(loginRequestDto);
  }

  @PostMapping("register")
  public CredentialsDto register(@RequestBody RegisterRequestDto registerRequestDto) {
    return userService.register(registerRequestDto);
  }
}
