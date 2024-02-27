package com.pawatask.task.kafka;

import com.pawatask.kafka.UserCreatedMessage;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static com.pawatask.kafka.KafkaTopics.Names.USER_CREATED;

@Component
public class UserCreatedConsumer {
    @KafkaListener(topics = USER_CREATED, groupId = "${kafka.consumer.group-id}")
    public void listenUserCreatedEvent(UserCreatedMessage message) {

    }
}