package com.pawatask.email.integrationTest;

import com.pawatask.email.domain.EmailService;
import com.pawatask.email.util.IntTestBase;
import com.pawatask.kafka.SendEmailMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.util.concurrent.CountDownLatch;

import static com.pawatask.kafka.EmailType.PASSWORD_RESET;
import static com.pawatask.kafka.KafkaTopics.Names.EMAIL;
import static java.time.Duration.ofSeconds;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.kafka.test.utils.ContainerTestUtils.waitForAssignment;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@EmbeddedKafka(
    topics = EMAIL,
    partitions = 1
)
@TestPropertySource(properties = {
    "kafka.bootstrap.servers=${spring.embedded.kafka.brokers}",
    "kafka.retry.delay=0"
})
@DirtiesContext
public class KafkaEmailTopicConsumerTest extends IntTestBase {
  @MockBean
  private EmailService emailService;

  @Autowired
  private KafkaTemplate<String, Object> kafkaTemplate;

  @Autowired
  private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

  @BeforeEach
  void waitForContainerAssignment() {
    for (var messageListenerContainer : kafkaListenerEndpointRegistry.getListenerContainers()) {
      waitForAssignment(messageListenerContainer, 1);
    }
  }

  @Test
  public void handleSendEmailMessage_noError() throws Exception {
    var latch = new CountDownLatch(1);

    doAnswer(invocation -> {
      latch.countDown();
      return null;
    }).when(emailService).send(argThat(x -> x.emailType() == PASSWORD_RESET));

    kafkaTemplate.send(EMAIL, new SendEmailMessage(null, PASSWORD_RESET, null, null, null));

    assertThat(latch.await(10, SECONDS)).isTrue();
    assertThat(loggerExtension.getFormattedMessages()).isEmpty();
  }

  @Test
  public void handleSendEmailMessage_deadLetterQueue() throws Exception {
    doThrow(new RuntimeException("Email sending error")).when(emailService).send(any());

    kafkaTemplate.send(EMAIL, new SendEmailMessage(null, null, null, null, null));

    await().atMost(ofSeconds(10)).until(() -> !loggerExtension.getFormattedMessages().isEmpty());
    assertThat(loggerExtension.getFormattedMessages()).first()
        .isEqualTo("Failed to send email after 3 retries. " +
            "Email body: SendEmailMessage[to=null, emailType=null, userId=Optional.empty, params=null, attachments=null], " +
            "Error: Listener failed; Email sending error");
  }
}
