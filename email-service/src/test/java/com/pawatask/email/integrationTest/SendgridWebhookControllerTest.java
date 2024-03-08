package com.pawatask.email.integrationTest;

import com.pawatask.email.domain.SendgridEventsService;
import com.pawatask.email.util.IntTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class SendgridWebhookControllerTest extends IntTestBase {
  @MockBean
  private SendgridEventsService sendgridEventsService;
  @Autowired
  private MockMvc mockMvc;

  @Test
  void process() throws Exception {
    String body = "body";
    String signature = "signature";
    String timestamp = "timestamp";

    mockMvc.perform(post("/sendgrid/processed-event")
            .content(body)
            .header("x-twilio-email-event-webhook-signature", signature)
            .header("x-twilio-email-event-webhook-timestamp", timestamp)
            .contentType(APPLICATION_JSON))
        .andExpect(status().isOk());

    verify(sendgridEventsService).process(body, signature, timestamp);
  }
}
