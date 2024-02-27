package com.pawatask.task.domain.task;

import com.pawatask.task.domain.userDetails.UserDetails;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.NotFound;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static org.hibernate.annotations.NotFoundAction.IGNORE;

@Data
@Entity
@Table(name = "task_comments")
public class TaskComment {
  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;
  private String comment;
  @Column(name = "created_at", insertable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "created_user_id")
  private Long createdByUserId;

  @ManyToOne
  @JoinColumn(
      name = "created_user_id",
      referencedColumnName = "id",
      insertable = false,
      updatable = false
  )
  @NotFound(action = IGNORE)
  @Nullable
  private UserDetails createdBy;

  @ManyToOne(fetch = LAZY)
  private Task task;
}
