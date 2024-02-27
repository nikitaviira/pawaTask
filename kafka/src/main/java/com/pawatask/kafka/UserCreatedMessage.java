package com.pawatask.kafka;

public record UserCreatedMessage(
    Long userId,
    String userName,
    String email
) implements KafkaJsonMessage {

}
