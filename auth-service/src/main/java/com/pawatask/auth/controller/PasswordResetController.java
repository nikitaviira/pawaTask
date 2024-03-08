package com.pawatask.auth.controller;

import com.pawatask.auth.domain.passwordReset.PasswordResetService;
import com.pawatask.auth.dto.NewPasswordDto;
import com.pawatask.auth.dto.PasswordResetDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class PasswordResetController extends BaseController {
  private final PasswordResetService passwordResetService;

  @PostMapping("password/reset")
  public void initPasswordReset(@Valid @RequestBody PasswordResetDto passwordResetDto) {
    passwordResetService.initPasswordReset(passwordResetDto);
  }

  @PostMapping("password/reset/{otp}/save-new")
  public void saveNewPassword(@Valid @RequestBody NewPasswordDto newPasswordDto, @PathVariable String otp) {
    passwordResetService.saveNewPassword(otp, newPasswordDto);
  }

  @GetMapping("password/reset/{otp}")
  public void verifyOtp(@PathVariable String otp) {
    passwordResetService.verifyAndGetOtp(otp);
  }
}
