package com.pawatask.email.domain;

import com.pawatask.email.config.EmailConfig;
import com.pawatask.kafka.SendEmailMessage;
import com.pawatask.kafka.SendEmailMessage.Attachment;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Attachments;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
  private final EmailConfig emailConfig;
  private final SendGridApi sendGridApi;

  @Value("${email.sendFrom}")
  private String sendFrom;
  @Value("${email.enableSending}")
  private boolean enableSending;

  public void send(SendEmailMessage message) throws IOException {
    if (enableSending) {
      String mailBody = mail(message).build();
      if (sendGridApi.send(mailBody)) {
        log.info("Sent email: {}", mailBody);
      }
    }
  }

  private Mail mail(SendEmailMessage message) {
    if (isBlank(message.to()) || isNull(message.emailType())) {
      throw new IllegalArgumentException();
    }

    String templateId = emailConfig.getTemplateId(message.emailType());

    var mail = new Mail();
    mail.setFrom(new Email(sendFrom));
    mail.setTemplateId(templateId);

    var personalization = new Personalization();
    setCustomArgs(personalization, message);
    setSubject(personalization, message);
    message.params().forEach(personalization::addDynamicTemplateData);
    personalization.addTo(new Email(message.to()));
    mail.addPersonalization(personalization);

    message.attachments().forEach(attachment -> addAttachment(mail, attachment));
    return mail;
  }

  private void setSubject(Personalization personalization, SendEmailMessage message) {
    String subject = emailConfig.getSubject(message.emailType());
    personalization.addDynamicTemplateData("subject", subject);
  }

  private void setCustomArgs(Personalization personalization, SendEmailMessage message) {
    personalization.addCustomArg("email_type", message.emailType().toString());
    message.userId().ifPresent(userId -> personalization.addCustomArg("user_id", userId.toString()));
  }

  private void addAttachment(Mail mail, Attachment attachment) {
    var attachments = new Attachments();
    attachments.setContent(attachment.encodedContent());
    attachments.setType(attachment.contentType());
    attachments.setFilename(attachment.fileName());
    mail.addAttachments(attachments);
  }
}
