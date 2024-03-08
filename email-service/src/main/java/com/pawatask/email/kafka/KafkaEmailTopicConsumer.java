package com.pawatask.email.kafka;

import com.pawatask.email.domain.EmailService;
import com.pawatask.kafka.SendEmailMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.pawatask.kafka.KafkaTopics.Names.EMAIL;

@Component
@RequiredArgsConstructor
@KafkaListener(topics = EMAIL, groupId = "${kafka.consumer.group-id}", concurrency = "3")
@Profile("!integration")
public class KafkaEmailTopicConsumer {
    private final EmailService emailService;

    @KafkaHandler
    @RetryableTopic(
        backoff = @Backoff(value = 3000L),
        autoCreateTopics = "false",
        include = IOException.class)
    public void handleSendEmailMessage(SendEmailMessage message) throws IOException {
        emailService.send(message);
    }
}