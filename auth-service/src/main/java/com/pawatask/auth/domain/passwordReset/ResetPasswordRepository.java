package com.pawatask.auth.domain.passwordReset;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface ResetPasswordRepository extends JpaRepository<ResetPasswordOtp, Long> {
  void deleteAllByEmail(String email);
  void deleteAllByCreatedAtBefore(Instant createdAt);
  Optional<ResetPasswordOtp> findByOtp(String otp);
}
