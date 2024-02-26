package com.pawatask.auth.user;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.IDENTITY;

@Data
@Entity
@Table(name = "users")
public class User {
  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;
  @Column(name = "hashed_password")
  private String hashedPassword;
  private String email;
  @Column(name = "user_name")
  private String userName;
  @Column(name = "created_at")
  private LocalDateTime createdAt;
}