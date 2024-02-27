package com.pawatask.auth.kafka;

import com.pawatask.kafka.KafkaJsonMessage;
import com.pawatask.kafka.KafkaTopics;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaMessageProducer {
    private final KafkaTemplate<String, KafkaJsonMessage> kafkaTemplate;

    public void sendMessage(KafkaTopics topic, KafkaJsonMessage message) {
        kafkaTemplate.send(topic.name(), message);
    }
}