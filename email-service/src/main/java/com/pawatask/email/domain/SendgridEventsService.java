package com.pawatask.email.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawatask.kafka.EmailType;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.pawatask.email.domain.SendgridEventsService.SendGridEvent.Status.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class SendgridEventsService {
  private final ObjectMapper objectMapper;
  private final SendgridSignatureVerifier sendgridSignatureVerifier;

  public void process(String body, String signature, String timestamp) {
    verifiedEvents(body, signature, timestamp).forEach(this::handleSendGridEvent);
  }

  public void handleSendGridEvent(SendGridEvent sendGridEvent) {
    if (sendGridEvent.isSuccessful()) {
      log.info("Sendgrid email successfully delivered. [Event: {}]", sendGridEvent);
    } else {
      log.warn("Sendgrid email failed to be delivered. [Event: {}]", sendGridEvent);
    }
  }

  @SneakyThrows
  private List<SendGridEvent> verifiedEvents(String body, String signature, String timestamp) {
    sendgridSignatureVerifier.verifySignature(body, signature, timestamp);
    return objectMapper.readValue(body, new TypeReference<>() {});
  }

  @ToString
  @AllArgsConstructor
  public static class SendGridEvent {
    public String email;

    @JsonProperty("event")
    public String name;

    @Nullable
    public String type;

    @Nullable
    public String reason;

    @JsonProperty("email_type")
    @Nullable
    public EmailType emailType;

    @JsonProperty("user_id")
    @Nullable
    public Long userId;

    public Status status() {
      return switch (this.name) {
        case "bounce", "dropped", "spamreport" -> FAILED;
        case "delivered" -> SUCCEEDED;
        default -> OTHER;
      };
    }

    public enum Status {
      FAILED, SUCCEEDED, OTHER
    }

    public boolean isSuccessful() {
      return status() == SUCCEEDED;
    }
  }
}
