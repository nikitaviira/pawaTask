package com.pawatask.task.kafka;

import com.pawatask.kafka.UserCreatedMessage;
import com.pawatask.task.domain.userDetails.UserDetails;
import com.pawatask.task.domain.userDetails.UserDetailsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static com.pawatask.kafka.KafkaTopics.Names.USER;

@Component
@RequiredArgsConstructor
@KafkaListener(topics = USER, groupId = "${kafka.consumer.group-id}")
public class KafkaUserTopicConsumer {
    private final UserDetailsRepository userDetailsRepository;

    @KafkaHandler
    public void handleUserCreatedMessage(UserCreatedMessage message) {
        var userDetails = new UserDetails();
        userDetails.setId(message.userId());
        userDetails.setUserName(message.userName());
        userDetails.setEmail(message.email());
        userDetailsRepository.save(userDetails);
    }
}