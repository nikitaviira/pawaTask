package com.pawatask.task.kafka;

import com.pawatask.kafka.UserCreatedMessage;
import com.pawatask.task.domain.userDetails.UserDetails;
import com.pawatask.task.domain.userDetails.UserDetailsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;

import static com.pawatask.kafka.KafkaTopics.Names.USER;
import static java.util.concurrent.Executors.newFixedThreadPool;

@Component
@RequiredArgsConstructor
@KafkaListener(topics = USER, groupId = "${kafka.consumer.group-id}", concurrency = "3")
@Slf4j
public class KafkaUserTopicConsumer {
    private final UserDetailsRepository userDetailsRepository;
    private final ExecutorService executorService = newFixedThreadPool(20);

    @KafkaHandler
    public void handleUserCreatedMessage(UserCreatedMessage message) {
        log.info("Received message=[" + message + "] for topic=[" + USER + "]");
        executorService.submit(() -> saveUserDetails(message));
    }

    private void saveUserDetails(UserCreatedMessage message) {
        var userDetails = new UserDetails();
        userDetails.setId(message.userId());
        userDetails.setUserName(message.userName());
        userDetails.setEmail(message.email());
        userDetailsRepository.save(userDetails);
    }
}