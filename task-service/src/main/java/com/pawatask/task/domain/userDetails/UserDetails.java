package com.pawatask.task.domain.userDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "user_details")
public class UserDetails {
  @Id
  private Long id;
  private String email;
  @Column(name = "user_name")
  private String userName;
}