package com.pawatask.auth.integrationTest.jobs;

import com.pawatask.auth.domain.passwordReset.ResetPasswordOtp;
import com.pawatask.auth.domain.passwordReset.ResetPasswordRepository;
import com.pawatask.auth.jobs.ClearExpiredResetPasswordOtpJob;
import com.pawatask.auth.util.IntTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;

import static com.pawatask.auth.util.DateUtil.setMockNow;
import static java.time.temporal.ChronoUnit.HOURS;
import static org.assertj.core.api.Assertions.assertThat;

public class ClearExpiredResetPasswordOtpJobTest extends IntTestBase {
  @Autowired
  private ResetPasswordRepository resetPasswordRepository;
  @Autowired
  private ClearExpiredResetPasswordOtpJob clearExpiredResetPasswordOtpJob;

  @Test
  public void deleteAllExpired() {
    var otp = new ResetPasswordOtp();
    otp.setOtp("otp");
    otp.setEmail("email");
    resetPasswordRepository.save(otp);

    clearExpiredResetPasswordOtpJob.executeJob();
    assertThat(resetPasswordRepository.count()).isOne();

    setMockNow(Instant.now().plus(3, HOURS));

    clearExpiredResetPasswordOtpJob.executeJob();
    assertThat(resetPasswordRepository.count()).isZero();
  }
}
