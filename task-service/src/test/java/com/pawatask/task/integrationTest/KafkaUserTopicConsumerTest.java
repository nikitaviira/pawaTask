package com.pawatask.task.integrationTest;

import com.pawatask.kafka.UserCreatedMessage;
import com.pawatask.task.domain.userDetails.UserDetails;
import com.pawatask.task.domain.userDetails.UserDetailsRepository;
import com.pawatask.task.util.IntTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static com.pawatask.kafka.KafkaTopics.Names.USER;
import static java.time.Duration.ofSeconds;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.kafka.test.utils.ContainerTestUtils.waitForAssignment;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@EmbeddedKafka(topics = USER, partitions = 1)
@TestPropertySource(properties = "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}")
@DirtiesContext
public class KafkaUserTopicConsumerTest extends IntTestBase {
  @Autowired
  private KafkaTemplate<String, Object> kafkaTemplate;

  @Autowired
  private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

  @Autowired
  private UserDetailsRepository userDetailsRepository;

  @BeforeEach
  void waitForContainerAssignment() {
    for (var messageListenerContainer : kafkaListenerEndpointRegistry.getListenerContainers()) {
      waitForAssignment(messageListenerContainer, 1);
    }
  }

  @Test
  public void handleUserCreatedMessage() {
    kafkaTemplate.send(USER, new UserCreatedMessage(1L, "username", "email"));

    await()
        .atMost(ofSeconds(10))
        .pollInterval(ofSeconds(1L))
        .untilAsserted(() -> {
          Optional<UserDetails> userDetails = userDetailsRepository.findById(1L);
          assertThat(userDetails).isPresent();
          assertThat(userDetails.get().getId()).isEqualTo(1L);
          assertThat(userDetails.get().getUserName()).isEqualTo("username");
          assertThat(userDetails.get().getEmail()).isEqualTo("email");
        });
  }
}
