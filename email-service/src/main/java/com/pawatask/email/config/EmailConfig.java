package com.pawatask.email.config;

import com.pawatask.kafka.EmailType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class EmailConfig {
    @Value("#{${email.templateId}}")
    private Map<EmailType, String> emailTemplates;

    @Value("#{${email.subject}}")
    private Map<EmailType, String> emailSubjects;

    public String getTemplateId(EmailType emailType) {
        return emailTemplates.get(emailType);
    }

    public String getSubject(EmailType emailType) {
        return emailSubjects.get(emailType);
    }
}