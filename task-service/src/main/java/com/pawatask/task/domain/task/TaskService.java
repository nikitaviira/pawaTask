package com.pawatask.task.domain.task;

import com.pawatask.task.domain.userDetails.UserDetails;
import com.pawatask.task.domain.userDetails.UserDetailsRepository;
import com.pawatask.task.dto.*;
import com.pawatask.task.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Function;

import static com.pawatask.task.util.DateUtil.*;
import static org.springframework.data.domain.Sort.by;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {
  private final TaskRepository taskRepository;
  private final UserDetailsRepository userDetailsRepository;

  public List<TaskDisplayDto> allTasks(SortOrder sortOrder) {
    return taskRepository.findAllBy(by(sortOrder.getDirection(), sortOrder.getDbField())).stream()
        .map(this::toTaskDisplayDto)
        .toList();
  }

  public TaskDto task(Long id) {
    return mapTaskToDto(id, this::toTaskDto);
  }

  public TaskDetailsDto taskDetails(Long id) {
    return mapTaskToDto(id, this::toTaskDetailsDto);
  }

  public void saveTask(CallerContext callerContext, TaskDto taskDto) {
    UserDetails user = userDetailsOrThrow(callerContext);
    Long taskId = taskDto.id();
    Task task = taskId != null
        ? taskRepository.findById(taskId).orElseThrow(() -> taskNotFound(taskId))
        : new Task();

    task.setTitle(taskDto.title());
    task.setDescription(taskDto.description());
    task.setPriority(taskDto.priority());
    task.setPriority(taskDto.priority());
    task.setDueDate(taskDto.dueDate());
    task.setLastEditedBy(user);
    if (taskId == null) {
      task.setCreatedBy(user);
    }

    taskRepository.save(task);
  }

  public void saveComment(CallerContext callerContext, SaveCommentDto comment) {
    UserDetails user = userDetailsOrThrow(callerContext);
    Long taskId = comment.taskId();
    Task task = taskRepository.findById(taskId).orElseThrow(() -> taskNotFound(taskId));

    var newComment = new TaskComment();
    newComment.setComment(comment.comment());
    newComment.setCreatedBy(user);
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

  private TaskDisplayDto toTaskDisplayDto(Task task) {
    return new TaskDisplayDto(
        task.getId(),
        task.getTitle(),
        task.getPriority(),
        task.getDueDate()
    );
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
        task.getCreatedBy().getUserName(),
        task.getLastEditedBy().getUserName(),
        formatDate(task.getDueDate()),
        task.getPriority(),
        task.getComments().stream().map(c -> new CommentDto(
            c.getComment(),
            c.getCreatedBy().getUserName(),
            formatFullDateTime(c.getCreatedAt()))
        ).toList()
    );
  }

  // TODO in case user details were not synced yet, sync them manually
  private UserDetails userDetailsOrThrow(CallerContext caller) {
    return userDetailsRepository.findById(caller.userId()).orElseThrow(() -> {
      log.error("User details missing for userId: %s; Initiating manual data sync...".formatted(caller.userId()));
      return new ServiceException("An error has happened");
    });
  }

  private ServiceException taskNotFound(Long id) {
    return new ServiceException("No task found with id: %s".formatted(id));
  }
}
