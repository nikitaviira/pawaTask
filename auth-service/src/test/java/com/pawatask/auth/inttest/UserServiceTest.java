package com.pawatask.auth.inttest;

import com.pawatask.auth.user.User;
import com.pawatask.auth.user.UserRepository;
import com.pawatask.auth.util.IntTestBase;
import com.pawatask.auth.util.PasswordHashing;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

  @Test
  public void register_success() throws Exception {
    String userJson = "{" +
        "\"email\": \"email@mail.ru\", " +
        "\"userName\": \"somename\", " +
        "\"password\" : \"mega\"}";

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
    assertTrue(passwordHashing.validatePassword("mega", user.get().getHashedPassword()));
  }

  @Test
  public void register_alreadyExist() throws Exception {
    saveUser();
    String userJson = "{" +
        "\"email\": \"email@mail.ru\", " +
        "\"userName\": \"somename\", " +
        "\"password\" : \"mega\"}";

    mockMvc.perform(post("/api/auth/register")
            .content(userJson)
            .contentType(APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath("$.message", is("User with such email already exists")));

    assertThat(userRepository.count()).isEqualTo(1);
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
