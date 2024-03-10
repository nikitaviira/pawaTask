package com.pawatask.auth.domain.passwordReset;

import com.pawatask.auth.domain.user.User;
import com.pawatask.auth.domain.user.UserRepository;
import com.pawatask.auth.dto.NewPasswordDto;
import com.pawatask.auth.dto.PasswordResetDto;
import com.pawatask.auth.exception.ServiceException;
import com.pawatask.auth.kafka.KafkaMessageProducer;
import com.pawatask.auth.util.PasswordHashing;
import com.pawatask.auth.util.RandomUniqueToken;
import com.pawatask.auth.util.UrlUtil;
import com.pawatask.kafka.SendEmailMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static com.pawatask.auth.util.DateUtil.now;
import static com.pawatask.kafka.EmailType.PASSWORD_RESET;
import static com.pawatask.kafka.KafkaTopics.EMAIL;
import static java.time.Duration.ofMinutes;
import static java.util.Optional.empty;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetService {
  public static final Duration PASSWORD_RESET_OTP_EXPIRATION = ofMinutes(20L);
  private final KafkaMessageProducer kafkaMessageProducer;
  private final UserRepository userRepository;
  private final PasswordHashing passwordHashing;
  private final ResetPasswordRepository resetPasswordRepository;
  private final RandomUniqueToken randomUniqueToken;
  private final UrlUtil urlUtil;

  @Transactional
  public void initPasswordReset(PasswordResetDto request) {
    if (userRepository.findByEmail(request.email()).isEmpty()) {
      return;
    }

    ResetPasswordOtp resetPasswordOtp = issueOtp(request.email());
    kafkaMessageProducer.sendMessage(EMAIL, new SendEmailMessage(
        resetPasswordOtp.getEmail(),
        PASSWORD_RESET,
        empty(),
        Map.of("resetPasswordUrl", resetPasswordUrl(resetPasswordOtp.getOtp())),
        List.of()
    ));
  }

  public ResetPasswordOtp verifyAndGetOtp(String otp) {
    ResetPasswordOtp resetPasswordOtp = resetPasswordRepository.findByOtp(otp).orElseThrow(this::missingOtp);
    if (hasExpired(resetPasswordOtp)) {
      throw expiredOtp();
    }
    return resetPasswordOtp;
  }

  @Transactional
  public void saveNewPassword(String otp, NewPasswordDto request) {
    ResetPasswordOtp resetPasswordOtp = verifyAndGetOtp(otp);
    User user = userRepository.findByEmail(resetPasswordOtp.getEmail()).orElseThrow(this::missingUser);

    String hashedPassword = passwordHashing.createHash(request.newPassword());
    user.setHashedPassword(hashedPassword);
    userRepository.save(user);
    resetPasswordRepository.deleteAllByEmail(resetPasswordOtp.getEmail());
    log.info("Password reset for user: {}", user);
  }

  private ResetPasswordOtp issueOtp(String email) {
    resetPasswordRepository.deleteAllByEmail(email);

    String randomToken = randomUniqueToken.generateRandomToken();
    var resetPasswordOtp = new ResetPasswordOtp();
    resetPasswordOtp.setOtp(randomToken);
    resetPasswordOtp.setEmail(email);

    return resetPasswordRepository.save(resetPasswordOtp);
  }

  private String resetPasswordUrl(String code) {
    return urlUtil.absoluteUrl("/reset-password/%s".formatted(code));
  }

  private boolean hasExpired(ResetPasswordOtp resetPasswordOtp) {
    return now().isAfter(validUntil(resetPasswordOtp));
  }

  private Instant validUntil(ResetPasswordOtp resetPasswordOtp) {
    return resetPasswordOtp.getCreatedAt().plus(PASSWORD_RESET_OTP_EXPIRATION);
  }

  private ServiceException missingOtp() {
    return new ServiceException("Password reset link is incorrect");
  }

  private ServiceException expiredOtp() {
    return new ServiceException("Password reset link has expired");
  }

  private ServiceException missingUser() {
    return new ServiceException("No user found with such email");
  }
}
