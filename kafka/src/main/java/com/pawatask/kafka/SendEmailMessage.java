package com.pawatask.kafka;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public record SendEmailMessage(
    String to,
    EmailType emailType,
    Optional<Long> userId,
    Map<String, Object> params,
    List<Attachment> attachments
) implements KafkaJsonMessage {
  public record Attachment(
      String encodedContent,
      String contentType,
      String fileName
  ) {}
}
