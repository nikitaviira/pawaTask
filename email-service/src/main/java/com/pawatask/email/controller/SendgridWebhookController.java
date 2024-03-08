package com.pawatask.email.controller;

import com.pawatask.email.domain.SendgridEventsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.sendgrid.helpers.eventwebhook.EventWebhookHeader.SIGNATURE;
import static com.sendgrid.helpers.eventwebhook.EventWebhookHeader.TIMESTAMP;

@RestController
@RequiredArgsConstructor
public class SendgridWebhookController {
  private final SendgridEventsService sendgridEventsService;

  @PostMapping("/sendgrid/processed-event")
  public void process(@RequestHeader Map<String, String> headers, @RequestBody String body) {
    String signature = headers.get(SIGNATURE.name.toLowerCase());
    String timestamp = headers.get(TIMESTAMP.name.toLowerCase());
    sendgridEventsService.process(body, signature, timestamp);
  }
}
