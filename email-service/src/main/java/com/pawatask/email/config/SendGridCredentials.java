package com.pawatask.email.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "sendgrid")
public class SendGridCredentials {
    private String apiKey;
    private String verificationKey;
}