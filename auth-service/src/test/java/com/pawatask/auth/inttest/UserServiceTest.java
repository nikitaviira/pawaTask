package com.pawatask.auth.inttest;

import com.pawatask.auth.user.User;
import com.pawatask.auth.user.UserRepository;
import com.pawatask.auth.util.IntTestBase;
import com.pawatask.auth.util.PasswordHashing;
import com.pawatask.kafka.UserCreatedMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static com.pawatask.kafka.KafkaTopics.Names.USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
class UserServiceTest extends IntTestBase {
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private PasswordHashing passwordHashing;
  @MockBean
  private KafkaTemplate<String, Object> kafkaProducer;

  @Test
  public void register_success() throws Exception {
    String userJson = "{" +
        "\"email\": \"email@mail.ru\", " +
        "\"userName\": \"somename\", " +
        "\"password\" : \"megamegA1\"}";

    mockMvc.perform(post("/api/auth/register")
            .content(userJson)
            .contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath("$.jwtToken").exists())
        .andExpect(jsonPath("$.jwtToken").isString());

    Optional<User> user = userRepository.findById(1L);
    assertThat(user).isPresent();
    assertThat(user.get().getEmail()).isEqualTo("email@mail.ru");
    assertThat(user.get().getUserName()).isEqualTo("somename");
    assertTrue(passwordHashing.validatePassword("megamegA1", user.get().getHashedPassword()));

    verify(kafkaProducer).send(USER, new UserCreatedMessage(1L, "somename", "email@mail.ru"));
  }

  @Test
  public void register_validationFailed() throws Exception {
    String userJson = "{" +
        "\"email\": \"wrong-email\", " +
        "\"userName\": \"" + "b".repeat(256) + "\", " +
        "\"password\" : \"abc\"}";

    mockMvc.perform(post("/api/auth/register")
            .content(userJson)
            .contentType(APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath("$.fields.password").value("At least 8 chars long, one capital letter and one number"))
        .andExpect(jsonPath("$.fields.userName").value("Exceeds maximum length of 255 symbols"))
        .andExpect(jsonPath("$.fields.email").value("Incorrect email format"));

    assertThat(userRepository.findAll()).isEmpty();
    verify(kafkaProducer, never()).send(any(), any());
  }

  @Test
  public void register_alreadyExist() throws Exception {
    saveUser();
    String userJson = "{" +
        "\"email\": \"email@mail.ru\", " +
        "\"userName\": \"somename\", " +
        "\"password\" : \"megamegA1\"}";

    mockMvc.perform(post("/api/auth/register")
            .content(userJson)
            .contentType(APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath("$.message", is("User with such email already exists")));

    assertThat(userRepository.count()).isEqualTo(1);
    verify(kafkaProducer, never()).send(any(), any());
  }

  @Test
  public void login_validationFailed() throws Exception {
    saveUser();
    String userJson = "{" +
        "\"email\": \"\", " +
        "\"password\" : \"\"}";

    mockMvc.perform(post("/api/auth/login")
            .content(userJson)
            .contentType(APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath("$.fields.email").value("Field is mandatory"))
        .andExpect(jsonPath("$.fields.password").value("Field is mandatory"));
  }

  @Test
  public void login_success() throws Exception {
    saveUser();
    String userJson = "{" +
        "\"email\": \"email@mail.ru\", " +
        "\"password\" : \"mega\"}";

    mockMvc.perform(post("/api/auth/login")
            .content(userJson)
            .contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath("$.jwtToken").exists())
        .andExpect(jsonPath("$.jwtToken").isString());
  }

  @Test
  public void login_wrongEmail() throws Exception {
    saveUser();
    String userJson = "{" +
        "\"email\": \"asdasdasd@mail.ru\", " +
        "\"password\" : \"mega\"}";

    mockMvc.perform(post("/api/auth/login")
            .content(userJson)
            .contentType(APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath("$.message", is("Incorrect email or password")));
  }

  @Test
  public void login_wrongPassword() throws Exception {
    saveUser();
    String userJson = "{" +
        "\"email\": \"email@mail.ru\", " +
        "\"password\" : \"asdsdas\"}";

    mockMvc.perform(post("/api/auth/login")
            .content(userJson)
            .contentType(APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath("$.message", is("Incorrect email or password")));
  }

  private void saveUser() {
    var existingUser = new User();
    existingUser.setEmail("email@mail.ru");
    existingUser.setUserName("somename");
    existingUser.setHashedPassword(passwordHashing.createHash("mega"));
    userRepository.save(existingUser);
  }
}
