package com.pawatask.email.integrationTest;

import com.pawatask.email.config.SendGridCredentials;
import com.pawatask.email.domain.SendgridEventsService;
import com.pawatask.email.domain.SendgridEventsService.SendGridEvent;
import com.pawatask.email.util.IntTestBase;
import com.pawatask.email.util.LoggerExtension;
import com.sendgrid.helpers.eventwebhook.EventWebhook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static com.pawatask.kafka.EmailType.PASSWORD_RESET;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

public class SendgridEventsServiceTest extends IntTestBase {
  @Autowired
  private SendgridEventsService sendgridEventsService;
  @MockBean
  private SendGridCredentials sendGridCredentials;
  @MockBean
  private EventWebhook eventWebhook;
  @RegisterExtension
  public LoggerExtension loggerExtension = new LoggerExtension();

  @Test
  void process() throws Exception {
    var event1 = new SendGridEvent("email1", "dropped", "type", "wrong", PASSWORD_RESET, 1L);
    var event2 = new SendGridEvent("email2", "delivered", null, null, PASSWORD_RESET, 2L);
    List<SendGridEvent> events = List.of(event1, event2);

    String body = convertObjectToJsonString(events);
    String signature = "signature";
    String timestamp = "timestamp";

    when(sendGridCredentials.getVerificationKey()).thenReturn("verification-key");
    when(eventWebhook.ConvertPublicKeyToECDSA("verification-key")).thenReturn(null);
    doReturn(true).when(eventWebhook).VerifySignature(null, body, signature, timestamp);

    sendgridEventsService.process(body, signature, timestamp);

    List<String> logs = loggerExtension.getFormattedMessages();
    assertThat(logs.getFirst()).contains("Sendgrid email failed to be delivered. [Event: SendgridEventsService.SendGridEvent(" +
        "email=email1, name=dropped, type=type, reason=wrong, emailType=PASSWORD_RESET, userId=1)]");
    assertThat(logs.get(1)).contains("Sendgrid email successfully delivered. [Event: SendgridEventsService.SendGridEvent(" +
        "email=email2, name=delivered, type=null, reason=null, emailType=PASSWORD_RESET, userId=2)]");
  }
}
