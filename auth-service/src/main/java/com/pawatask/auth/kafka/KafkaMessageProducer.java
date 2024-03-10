package com.pawatask.auth.kafka;

import com.pawatask.kafka.KafkaJsonMessage;
import com.pawatask.kafka.KafkaTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaMessageProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendMessage(KafkaTopics topic, KafkaJsonMessage message) {
        kafkaTemplate.send(topic.label, message).whenComplete((result, throwable) -> {
            if (throwable != null) {
                log.error("Unable to send message=[{}] to topic=[{}] due to : {}", message, topic, throwable.getMessage());
            } else {
                log.info("Sent message=[{}] to topic=[{}] with offset=[{}]", message, topic, result.getRecordMetadata().offset());
            }
        });
    }
}