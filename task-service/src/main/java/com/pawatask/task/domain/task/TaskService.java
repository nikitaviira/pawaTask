package com.pawatask.task.domain.task;

import com.pawatask.task.domain.userDetails.UserDetails;
import com.pawatask.task.dto.*;
import com.pawatask.task.exception.ServiceException;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Function;

import static com.pawatask.task.util.DateUtil.*;
import static java.util.Optional.ofNullable;
import static org.springframework.data.domain.Sort.by;

@Service
@RequiredArgsConstructor
public class TaskService {
  private final TaskRepository taskRepository;

  public List<TaskDto> allTasks(SortOrder sortOrder) {
    return taskRepository.findAllBy(by(sortOrder.getDirection(), sortOrder.getDbField())).stream()
        .map(this::toTaskDto)
        .toList();
  }

  public TaskDto task(Long id) {
    return mapTaskToDto(id, this::toTaskDto);
  }

  public TaskDetailsDto taskDetails(Long id) {
    return mapTaskToDto(id, this::toTaskDetailsDto);
  }

  public void saveTask(CallerContext callerContext, TaskDto taskDto) {
    Long taskId = taskDto.id();
    Task task = taskId != null
        ? taskRepository.findById(taskId).orElseThrow(() -> taskNotFound(taskId))
        : new Task();

    task.setTitle(taskDto.title());
    task.setDescription(taskDto.description());
    task.setPriority(taskDto.priority());
    task.setPriority(taskDto.priority());
    task.setDueDate(taskDto.dueDate());
    task.setCreatedByUserId(callerContext.userId());
    task.setLastEditedByUserId(callerContext.userId());
    taskRepository.save(task);
  }

  public void saveComment(CallerContext callerContext, SaveCommentDto comment) {
    Long taskId = comment.taskId();
    Task task = taskRepository.findById(taskId).orElseThrow(() -> taskNotFound(taskId));

    var newComment = new TaskComment();
    newComment.setComment(comment.comment());
    newComment.setCreatedByUserId(callerContext.userId());
    task.addComment(newComment);
    taskRepository.save(task);
  }

  @Transactional
  public void deleteTasks(List<Long> idsToDelete) {
    taskRepository.deleteAllByIdIn(idsToDelete);
  }

  private <T> T mapTaskToDto(Long id, Function<Task, T> mapper) {
    return taskRepository.findById(id)
        .map(mapper)
        .orElseThrow(() -> taskNotFound(id));
  }

  private TaskDto toTaskDto(Task task) {
    return new TaskDto(
        task.getId(),
        task.getTitle(),
        task.getDescription(),
        task.getPriority(),
        task.getDueDate()
    );
  }

  private TaskDetailsDto toTaskDetailsDto(Task task) {
    return new TaskDetailsDto(
        task.getId(),
        task.getTitle(),
        task.getDescription(),
        formatDateTime(task.getCreatedAt()),
        getUserName(task.getCreatedBy()),
        getUserName(task.getLastEditedBy()),
        formatDate(task.getDueDate()),
        task.getPriority(),
        task.getComments().stream().map(c -> new CommentDto(
            c.getComment(),
            getUserName(c.getCreatedBy()),
            formatFullDateTime(c.getCreatedAt()))
        ).toList()
    );
  }

  @Nullable private String getUserName(@Nullable UserDetails userDetails) {
    return ofNullable(userDetails).map(UserDetails::getUserName).orElse(null);
  }

  private ServiceException taskNotFound(Long id) {
    return new ServiceException("No task found with id: %s".formatted(id));
  }
}
