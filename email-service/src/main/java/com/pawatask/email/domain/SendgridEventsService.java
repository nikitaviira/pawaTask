package com.pawatask.email.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawatask.email.config.SendGridCredentials;
import com.pawatask.kafka.EmailType;
import com.sendgrid.helpers.eventwebhook.EventWebhook;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.interfaces.ECPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

import static com.pawatask.email.domain.SendgridEventsService.SendGridEvent.Status.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class SendgridEventsService {
  private final SendGridCredentials sendGridCredentials;
  private final ObjectMapper objectMapper;
  private final EventWebhook eventWebhook;

  static {
    Security.addProvider(new BouncyCastleProvider());
  }

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
    try {
      eventWebhook.VerifySignature(publicKey(eventWebhook), body, signature, timestamp);
    }
    catch (Exception e) {
      throw new RuntimeException("Signature verification failed", e);
    }
    return objectMapper.readValue(body, new TypeReference<>() {});
  }

  private ECPublicKey publicKey(EventWebhook eventWebhook) {
    try {
      return eventWebhook.ConvertPublicKeyToECDSA(sendGridCredentials.getVerificationKey());
    }
    catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchProviderException e) {
      throw new RuntimeException(e);
    }
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
