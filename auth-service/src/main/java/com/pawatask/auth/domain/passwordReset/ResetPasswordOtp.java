package com.pawatask.auth.domain.passwordReset;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

import static jakarta.persistence.GenerationType.IDENTITY;

@Data
@Entity
@Table(name = "reset_password_otps")
public class ResetPasswordOtp {
  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;
  @Column(name = "created_at", insertable = false, updatable = false)
  private Instant createdAt;
  private String otp;
  private String email;
}
