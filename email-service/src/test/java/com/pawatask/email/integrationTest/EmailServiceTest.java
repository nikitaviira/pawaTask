package com.pawatask.email.integrationTest;

import com.pawatask.email.domain.EmailService;
import com.pawatask.email.domain.SendGridApi;
import com.pawatask.email.util.IntTestBase;
import com.pawatask.kafka.SendEmailMessage;
import com.pawatask.kafka.SendEmailMessage.Attachment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.pawatask.kafka.EmailType.PASSWORD_RESET;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@TestPropertySource(properties = "email.enableSending=true")
public class EmailServiceTest extends IntTestBase {
  @Autowired
  private EmailService emailService;
  @MockBean
  private SendGridApi sendGridApi;

  @Test
  void send() throws IOException {
    var message = new SendEmailMessage(
        "email@email.to",
        PASSWORD_RESET,
        Optional.of(1L),
        Map.of("customParam", "paramValue"),
        List.of(new Attachment("content", "pdf", "file"))
    );

    emailService.send(message);

    verify(sendGridApi).send(eq("{\"" +
        "from\":{\"email\":\"nikita.viira.site@gmail.com\"}," +
        "\"personalizations\":[{\"to\":[{\"email\":\"email@email.to\"}]," +
        "\"custom_args\":{\"email_type\":\"PASSWORD_RESET\",\"user_id\":\"1\"}," +
        "\"dynamic_template_data\":{\"customParam\":\"paramValue\",\"subject\":\"Password reset request\"}}]," +
        "\"attachments\":[{\"content\":\"content\",\"type\":\"pdf\",\"filename\":\"file\"}]," +
        "\"template_id\":\"d-aeb58f2df5e34b74a88f23e40ae8f1a6\"}"));
  }
}
