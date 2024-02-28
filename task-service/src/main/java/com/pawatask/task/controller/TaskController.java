package com.pawatask.task.controller;

import com.pawatask.task.domain.task.TaskService;
import com.pawatask.task.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TaskController extends BaseController {
  private final TaskService taskService;

  @GetMapping("all")
  public List<TaskDto> allTasks(@RequestParam(required = false, defaultValue = "DEFAULT") SortOrder sortOrder) {
    return taskService.allTasks(sortOrder);
  }

  @GetMapping("{id}")
  public TaskDto task(@PathVariable Long id) {
    return taskService.task(id);
  }

  @GetMapping("{id}/details")
  public TaskDetailsDto taskDetails(@PathVariable Long id) {
    return taskService.taskDetails(id);
  }

  @PostMapping("save")
  public void saveTask(CallerContext callerContext, @Valid @RequestBody TaskDto task) {
    taskService.saveTask(callerContext, task);
  }

  @PostMapping("comments/save")
  public void saveComment(CallerContext callerContext, @Valid @RequestBody SaveCommentDto comment) {
    taskService.saveComment(callerContext, comment);
  }

  @PostMapping("delete")
  public void deleteTasks(@RequestBody List<Long> idsToDelete) {
    taskService.deleteTasks(idsToDelete);
  }
}
