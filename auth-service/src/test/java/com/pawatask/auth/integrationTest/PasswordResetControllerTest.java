package com.pawatask.auth.integrationTest;

import com.pawatask.auth.domain.passwordReset.ResetPasswordOtp;
import com.pawatask.auth.domain.passwordReset.ResetPasswordRepository;
import com.pawatask.auth.domain.user.User;
import com.pawatask.auth.domain.user.UserRepository;
import com.pawatask.auth.dto.NewPasswordDto;
import com.pawatask.auth.dto.PasswordResetDto;
import com.pawatask.auth.kafka.KafkaMessageProducer;
import com.pawatask.auth.util.IntTestBase;
import com.pawatask.auth.util.PasswordHashing;
import com.pawatask.auth.util.RandomUniqueToken;
import com.pawatask.kafka.SendEmailMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.pawatask.auth.util.DateUtil.setMockNow;
import static com.pawatask.kafka.EmailType.PASSWORD_RESET;
import static com.pawatask.kafka.KafkaTopics.EMAIL;
import static java.time.temporal.ChronoUnit.HOURS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
public class PasswordResetControllerTest extends IntTestBase {
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private PasswordHashing passwordHashing;
  @Autowired
  private ResetPasswordRepository resetPasswordRepository;
  @MockBean
  private KafkaMessageProducer kafkaMessageProducer;
  @MockBean
  private RandomUniqueToken randomUniqueToken;

  @Test
  public void verifyOtp_success() throws Exception {
    String otp = "otp";
    saveResetPasswordOtp("email", otp);

    mockMvc.perform(get("/api/auth/password/reset/" + otp))
        .andExpect(status().isOk());
  }

  @Test
  public void verifyOtp_missing() throws Exception {
    mockMvc.perform(get("/api/auth/password/reset/random-otp"))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath("$.message", is("Password reset link is incorrect")));
  }

  @Test
  public void verifyOtp_expired() throws Exception {
    String otp = "otp";
    saveResetPasswordOtp("email", otp);
    setMockNow(Instant.now().plus(1, HOURS));

    mockMvc.perform(get("/api/auth/password/reset/" + otp))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath("$.message", is("Password reset link has expired")));
  }

  @Test
  public void initPasswordReset_success() throws Exception {
    String email = "email@email";
    saveUser(email);
    saveResetPasswordOtp(email, "some-otp-1");

    when(randomUniqueToken.generateRandomToken()).thenReturn("token");

    String body = convertObjectToJsonString(new PasswordResetDto(email));
    mockMvc.perform(post("/api/auth/password/reset")
            .content(body)
            .contentType(APPLICATION_JSON))
        .andExpect(status().isOk());

    List<ResetPasswordOtp> allOtps = resetPasswordRepository.findAll();
    assertThat(allOtps).hasSize(1);
    assertThat(allOtps.getFirst().getEmail()).isEqualTo(email);
    assertThat(allOtps.getFirst().getOtp()).isEqualTo("token");

    verify(kafkaMessageProducer).sendMessage(EMAIL, new SendEmailMessage(
        email,
        PASSWORD_RESET,
        Optional.empty(),
        Map.of("resetPasswordUrl", "http://localhost:5173/reset-password/token"),
        List.of()
    ));
  }

  @Test
  public void initPasswordReset_userMissing() throws Exception {
    String email = "email@email";
    when(randomUniqueToken.generateRandomToken()).thenReturn("token");

    String body = convertObjectToJsonString(new PasswordResetDto(email));
    mockMvc.perform(post("/api/auth/password/reset")
            .content(body)
            .contentType(APPLICATION_JSON))
        .andExpect(status().isOk());

    assertThat(resetPasswordRepository.count()).isZero();
    verify(kafkaMessageProducer, never()).sendMessage(any(), any());
  }

  @Test
  public void initPasswordReset_validationFailed() throws Exception {
    String body = convertObjectToJsonString(new PasswordResetDto(null));
    mockMvc.perform(post("/api/auth/password/reset")
            .content(body)
            .contentType(APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath("$.fields.email").value("Field is mandatory"));
  }

  @Test
  public void saveNewPassword_success() throws Exception {
    String otp = "otp";
    String email = "email@mail.ru";
    saveUser(email);
    saveResetPasswordOtp(email, otp);

    String body = convertObjectToJsonString(new NewPasswordDto("12345678Ab"));
    mockMvc.perform(post("/api/auth/password/reset/" + otp + "/save-new")
            .content(body)
            .contentType(APPLICATION_JSON))
        .andExpect(status().isOk());

    Optional<User> user = userRepository.findById(1L);
    assertThat(user).isPresent();
    assertThat(passwordHashing.validatePassword("12345678Ab", user.get().getHashedPassword())).isTrue();
    assertThat(resetPasswordRepository.count()).isZero();
  }

  @Test
  public void saveNewPassword_invalidOtp() throws Exception {
    String body = convertObjectToJsonString(new NewPasswordDto("12345678Ab"));
    mockMvc.perform(post("/api/auth/password/reset/wrong-otp/save-new")
            .content(body)
            .contentType(APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath("$.message").value("Password reset link is incorrect"));
  }

  @Test
  public void saveNewPassword_validationFailed() throws Exception {
    String body = convertObjectToJsonString(new NewPasswordDto(null));
    mockMvc.perform(post("/api/auth/password/reset/otp/save-new")
            .content(body)
            .contentType(APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath("$.fields.newPassword", hasItem("Field is mandatory")))
        .andExpect(jsonPath("$.fields.newPassword", hasItem("At least 8 chars long, one capital letter and one number")));
  }

  @Test
  public void saveNewPassword_missingUser() throws Exception {
    String otp = "otp";
    saveUser("email@mail.ru");
    saveResetPasswordOtp("other@email.ru", otp);

    String body = convertObjectToJsonString(new NewPasswordDto("12345678Ab"));
    mockMvc.perform(post("/api/auth/password/reset/" + otp + "/save-new")
            .content(body)
            .contentType(APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath("$.message").value("No user found with such email"));
  }

  private void saveResetPasswordOtp(String email, String otp) {
    var resetPasswordOtp = new ResetPasswordOtp();
    resetPasswordOtp.setOtp(otp);
    resetPasswordOtp.setEmail(email);
    resetPasswordRepository.save(resetPasswordOtp);
  }

  private void saveUser(String email) {
    var existingUser = new User();
    existingUser.setEmail(email);
    existingUser.setUserName("somename");
    existingUser.setHashedPassword(passwordHashing.createHash("mega"));
    userRepository.save(existingUser);
  }
}
