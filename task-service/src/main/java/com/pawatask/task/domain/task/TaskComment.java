package com.pawatask.task.domain.task;

import com.pawatask.task.domain.userDetails.UserDetails;
import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Data
@Entity
@Table(name = "task_comments")
public class TaskComment {
  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;
  private String comment;
  @Column(name = "created_at", insertable = false, updatable = false)
  private Instant createdAt;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "created_user_id")
  private UserDetails createdBy;

  @ManyToOne(fetch = LAZY)
  private Task task;
}
