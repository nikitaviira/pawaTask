package com.pawatask.auth.jobs;

import com.pawatask.auth.domain.passwordReset.ResetPasswordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static com.pawatask.auth.domain.passwordReset.PasswordResetService.PASSWORD_RESET_OTP_EXPIRATION;
import static com.pawatask.auth.util.DateUtil.now;

@Component
@RequiredArgsConstructor
public class ClearExpiredResetPasswordOtpJob {
  private final ResetPasswordRepository resetPasswordRepository;

  @Scheduled(cron = "0 0 0 * * ?")
  @Transactional
  public void executeJob() {
    Instant expirationTime = now().minus(PASSWORD_RESET_OTP_EXPIRATION);
    resetPasswordRepository.deleteAllByCreatedAtBefore(expirationTime);
  }
}
