package com.pawatask.task.domain.task;

import com.pawatask.task.domain.userDetails.UserDetails;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Data
@Entity
@Table(name = "tasks")
@ToString(exclude = {"comments", "createdBy", "lastEditedBy"})
public class Task {
  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;
  private String title;
  private String description;
  @Column(name = "created_at", insertable = false, updatable = false)
  private Instant createdAt;
  @Column(name = "due_date")
  private LocalDate dueDate;
  private TaskPriority priority;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "created_user_id")
  private UserDetails createdBy;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "last_edited_user_id")
  private UserDetails lastEditedBy;

  @OneToMany(cascade = ALL, orphanRemoval = true, mappedBy = "task")
  @OrderBy(value = "createdAt DESC")
  private List<TaskComment> comments = new ArrayList<>();

  public void addComment(TaskComment taskComment) {
    comments.add(taskComment);
    taskComment.setTask(this);
  }
}
