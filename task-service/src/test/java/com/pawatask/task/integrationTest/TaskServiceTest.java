package com.pawatask.task.integrationTest;

import com.pawatask.task.domain.task.*;
import com.pawatask.task.domain.userDetails.UserDetails;
import com.pawatask.task.domain.userDetails.UserDetailsRepository;
import com.pawatask.task.dto.SaveCommentDto;
import com.pawatask.task.dto.TaskDisplayDto;
import com.pawatask.task.dto.TaskDto;
import com.pawatask.task.util.IntTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.pawatask.task.domain.task.TaskPriority.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
class TaskServiceTest extends IntTestBase {
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private TaskRepository taskRepository;
  @Autowired
  private UserDetailsRepository userDetailsRepository;
  @Autowired
  private TaskService taskService;

  @Test
  public void saveTask_success() throws Exception {
    saveUserDetails(1L, "a@b.ra", "user_name");

    String body = convertObjectToJsonString(new TaskDto(null, "Water the plants", "Some description",
        MEDIUM, LocalDate.of(2022, 2, 25)));
    mockMvc.perform(post("/api/task/save")
            .content(body)
            .header("userId", 1L)
            .contentType(APPLICATION_JSON))
        .andExpect(status().isOk());

    Optional<Task> task = taskRepository.findById(1L);
    assertThat(task).isPresent();
    assertThat(task.get().getCreatedAt()).isNotNull();
    assertThat(task.get().getTitle()).isEqualTo("Water the plants");
    assertThat(task.get().getDescription()).isEqualTo("Some description");
    assertThat(task.get().getDueDate()).isEqualTo(LocalDate.of(2022, 2, 25));
    assertThat(task.get().getPriority()).isEqualTo(MEDIUM);
    assertThat(task.get().getCreatedBy().getUserName()).isEqualTo("user_name");
    assertThat(task.get().getLastEditedBy().getUserName()).isEqualTo("user_name");
  }

  @Test
  public void saveTask_updateExisting() throws Exception {
    UserDetails initialUser = saveUserDetails(1L, "a@b.ra", "user_name");
    UserDetails updateUser = saveUserDetails(2L, "a@bc.ra", "other_user_name");
    saveTask("Water the plants", LocalDate.of(2022, 2, 25), CRITICAL, initialUser);

    String body = convertObjectToJsonString(new TaskDto(1L, "Water the plants twice", "Some other description",
        LOW, LocalDate.of(2023, 2, 25)));
    mockMvc.perform(post("/api/task/save")
            .content(body)
            .header("userId", 2L)
            .contentType(APPLICATION_JSON))
        .andExpect(status().isOk());

    Optional<Task> task = taskRepository.findById(1L);
    assertThat(task).isPresent();
    assertThat(task.get().getCreatedAt()).isNotNull();
    assertThat(task.get().getTitle()).isEqualTo("Water the plants twice");
    assertThat(task.get().getDescription()).isEqualTo("Some other description");
    assertThat(task.get().getDueDate()).isEqualTo(LocalDate.of(2023, 2, 25));
    assertThat(task.get().getPriority()).isEqualTo(LOW);
    assertThat(task.get().getCreatedBy().getUserName()).isEqualTo("user_name");
    assertThat(task.get().getLastEditedBy().getUserName()).isEqualTo("other_user_name");
  }

  @Test
  public void saveTask_validationError() throws Exception {
    String body = convertObjectToJsonString(new TaskDto(null, "", "b".repeat(1001), null, null));
    mockMvc.perform(post("/api/task/save")
            .content(body)
            .header("userId", 1L)
            .contentType(APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath("$.fields.dueDate").value("Field is mandatory"))
        .andExpect(jsonPath("$.fields.description").value("Exceeds maximum length of 1000 symbols"))
        .andExpect(jsonPath("$.fields.priority").value("Field is mandatory"))
        .andExpect(jsonPath("$.fields.title").value("Field is mandatory"));
  }

  @Test
  public void saveComment_success() throws Exception {
    UserDetails user = saveUserDetails(1L, "a@b.ra", "user_name");
    Task existingTask = saveTask("Water the plants", LocalDate.of(2022, 2, 25), CRITICAL, user);

    String body = convertObjectToJsonString(new SaveCommentDto(1L, "Some comment"));
    mockMvc.perform(post("/api/task/comments/save")
            .content(body)
            .header("userId", 1L)
            .contentType(APPLICATION_JSON))
        .andExpect(status().isOk());

    Optional<Task> task = taskRepository.findById(1L);
    assertThat(task).isPresent();

    List<TaskComment> comments = task.get().getComments();
    assertThat(comments).hasSize(1);

    TaskComment comment = comments.getFirst();
    assertThat(comment.getComment()).isEqualTo("Some comment");
    assertThat(comment.getCreatedBy()).isEqualTo(user);
  }

  @Test
  public void saveComment_validationError() throws Exception {
    String body = convertObjectToJsonString(new SaveCommentDto(null, "a".repeat(200)));
    mockMvc.perform(post("/api/task/comments/save")
            .content(body)
            .header("userId", 1L)
            .contentType(APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath("$.fields.taskId").value("Field is mandatory"))
        .andExpect(jsonPath("$.fields.comment").value("Exceeds maximum length of 150 symbols"));
  }

  @Test
  public void allTasks() throws Exception {
    UserDetails user = saveUserDetails(1L, "a@b.ra", "user_name");
    saveTask("Water the plants", LocalDate.of(2022, 2, 25), CRITICAL, user);
    saveTask("Throw the garbage out", LocalDate.of(2022, 3, 25), MEDIUM, user);
    saveTask("Go touch grass", LocalDate.of(2024, 2, 25), HIGH, user);
    saveTask("Play games", LocalDate.of(2022, 2, 26), LOW, user);

    TaskDisplayDto t1 = new TaskDisplayDto(1L, "Water the plants", CRITICAL, LocalDate.of(2022, 2, 25));
    TaskDisplayDto t2 = new TaskDisplayDto(2L, "Throw the garbage out", MEDIUM, LocalDate.of(2022, 3, 25));
    TaskDisplayDto t3 = new TaskDisplayDto(3L, "Go touch grass", HIGH, LocalDate.of(2024, 2, 25));
    TaskDisplayDto t4 = new TaskDisplayDto(4L, "Play games", LOW, LocalDate.of(2022, 2, 26));

    mockMvc.perform(get("/api/task/all"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(content().json(convertObjectToJsonString(List.of(t1, t2, t3, t4)), true));

    mockMvc.perform(get("/api/task/all?sortOrder=PRIORITY_ASC")
            .contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(content().json(convertObjectToJsonString(List.of(t4, t2, t3, t1)), true));

    mockMvc.perform(get("/api/task/all?sortOrder=PRIORITY_DESC")
            .contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(content().json(convertObjectToJsonString(List.of(t1, t3, t2, t4)), true));

    mockMvc.perform(get("/api/task/all?sortOrder=DUE_DATE_ASC")
            .contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(content().json(convertObjectToJsonString(List.of(t1, t4, t2, t3)), true));

    mockMvc.perform(get("/api/task/all?sortOrder=DUE_DATE_DESC")
            .contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(content().json(convertObjectToJsonString(List.of(t3, t2, t4, t1)), true));
  }

  @Test
  public void task() throws Exception {
    UserDetails user = saveUserDetails(1L, "a@b.ra", "user_name");
    saveTask("Water the plants", LocalDate.of(2022, 2, 25), CRITICAL, user);

    mockMvc.perform(get("/api/task/1"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.title").value("Water the plants"))
        .andExpect(jsonPath("$.description").value("Some description"))
        .andExpect(jsonPath("$.priority").value("CRITICAL"))
        .andExpect(jsonPath("$.dueDate").value("2022-02-25"));
  }

  @Test
  public void taskDetails() throws Exception {
    UserDetails user = saveUserDetails(1L, "a@b.ra", "user_name");
    Task task = saveTask("Water the plants", LocalDate.of(2022, 2, 25), CRITICAL, user);
    saveComment(task, taskComment(user));

    mockMvc.perform(get("/api/task/1/details"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.title").value("Water the plants"))
        .andExpect(jsonPath("$.description").value("Some description"))
        .andExpect(jsonPath("$.priority").value("CRITICAL"))
        .andExpect(jsonPath("$.dueDate").value("25.02.2022"))
        .andExpect(jsonPath("$.createdAt").exists())
        .andExpect(jsonPath("$.createdBy").value("user_name"))
        .andExpect(jsonPath("$.lastEditedBy").value("user_name"))
        .andExpect(jsonPath("$.comments[0].comment").value("Some comment"))
        .andExpect(jsonPath("$.comments[0].createdBy").value("user_name"))
        .andExpect(jsonPath("$.comments[0].createdAt").exists());
  }

  @Test
  public void deleteTasks() throws Exception {
    UserDetails user = saveUserDetails(1L, "a@b.ra", "user_name");
    saveTask("Water the plants", LocalDate.of(2022, 2, 25), CRITICAL, user);
    saveTask("Throw the garbage out", LocalDate.of(2022, 3, 25), MEDIUM, user);

    assertThat(taskRepository.count()).isEqualTo(2);

    mockMvc.perform(post("/api/task/delete")
            .content(convertObjectToJsonString(List.of(1, 2)))
            .contentType(APPLICATION_JSON))
        .andExpect(status().isOk());

    assertThat(taskRepository.count()).isEqualTo(0);
  }

  private Task saveTask(String title, LocalDate dueDate, TaskPriority taskPriority, UserDetails user) {
    var task = new Task();
    task.setTitle(title);
    task.setDescription("Some description");
    task.setDueDate(dueDate);
    task.setPriority(taskPriority);
    task.setCreatedBy(user);
    task.setLastEditedBy(user);
    return taskRepository.save(task);
  }

  private UserDetails saveUserDetails(Long id, String email, String userName) {
    var user = new UserDetails();
    user.setId(id);
    user.setEmail(email);
    user.setUserName(userName);
    return userDetailsRepository.save(user);
  }

  private TaskComment taskComment(UserDetails user) {
    var taskComment = new TaskComment();
    taskComment.setComment("Some comment");
    taskComment.setCreatedBy(user);
    return taskComment;
  }

  private void saveComment(Task task, TaskComment taskComment) {
    task.addComment(taskComment);
    taskRepository.save(task);
  }
}
