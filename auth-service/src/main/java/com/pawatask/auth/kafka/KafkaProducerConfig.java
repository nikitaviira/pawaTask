package com.pawatask.auth.kafka;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin.NewTopics;

import static com.pawatask.kafka.KafkaTopics.Names.EMAIL;
import static com.pawatask.kafka.KafkaTopics.Names.USER;

@Configuration
public class KafkaProducerConfig {
    @Bean
    public NewTopics topics() {
        return new NewTopics(
            TopicBuilder.name(USER).replicas(1).partitions(6).build(),
            TopicBuilder.name(EMAIL).replicas(1).partitions(6).build()
        );
    }
}