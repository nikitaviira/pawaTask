package com.pawatask.email.kafka;

import com.pawatask.email.domain.EmailService;
import com.pawatask.kafka.SendEmailMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.pawatask.kafka.KafkaTopics.Names.EMAIL;
import static org.springframework.kafka.support.KafkaHeaders.EXCEPTION_MESSAGE;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaEmailTopicConsumer {
    private final EmailService emailService;

    @KafkaListener(
        topics = EMAIL,
        groupId = "${kafka.consumer.group-id}",
        concurrency = "3"
    )
    @RetryableTopic(
        backoff = @Backoff(
            delayExpression = "${kafka.retry.delay:3000}",
            multiplier = 1.5
        ),
        autoCreateTopics = "false"
    )
    @KafkaHandler
    public void handleSendEmailMessage(SendEmailMessage message) throws IOException {
        emailService.send(message);
    }

    @DltHandler
    public void handleDltSendEmailMessage(SendEmailMessage message,
                                          @Header(EXCEPTION_MESSAGE) String errorMessage) {
        log.warn("Failed to send email after 3 retries. Email body: {}, Error: {}", message, errorMessage);
    }
}