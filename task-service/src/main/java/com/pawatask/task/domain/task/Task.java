package com.pawatask.task.domain.task;

import com.pawatask.task.domain.userDetails.UserDetails;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.NotFound;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.GenerationType.IDENTITY;
import static org.hibernate.annotations.NotFoundAction.IGNORE;

@Data
@Entity
@Table(name = "tasks")
public class Task {
  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;
  private String title;
  private String description;
  @Column(name = "created_at", insertable = false, updatable = false)
  private LocalDateTime createdAt;
  @Column(name = "due_date")
  private LocalDate dueDate;
  private TaskPriority priority;

  @Column(name = "created_user_id")
  private Long createdByUserId;
  @Column(name = "last_edited_user_id")
  private Long lastEditedByUserId;

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

  @ManyToOne
  @JoinColumn(
      name = "last_edited_user_id",
      referencedColumnName = "id",
      insertable = false,
      updatable = false
  )
  @NotFound(action = IGNORE)
  @Nullable
  private UserDetails lastEditedBy;

  @OneToMany(cascade = ALL, orphanRemoval = true, mappedBy = "task")
  private List<TaskComment> comments = new ArrayList<>();

  public void addComment(TaskComment taskComment) {
    comments.add(taskComment);
    taskComment.setTask(this);
  }
}
