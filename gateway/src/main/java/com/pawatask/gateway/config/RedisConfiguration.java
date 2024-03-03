package com.pawatask.gateway.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import static org.springframework.data.redis.serializer.StringRedisSerializer.UTF_8;

@Configuration
@RequiredArgsConstructor
public class RedisConfiguration {
  private final ObjectMapper objectMapper;
  @Value("${redis.host}")
  private String host;
  @Value("${redis.port}")
  private int port;

  @Bean
  @Primary
  public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory() {
    return new LettuceConnectionFactory(host, port);
  }

  @Bean
  ReactiveRedisTemplate<String, Long> reactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {
    JdkSerializationRedisSerializer jdkSerializationRedisSerializer = new JdkSerializationRedisSerializer();
    GenericToStringSerializer<Long> longToStringSerializer = new GenericToStringSerializer<>(Long.class);
    return new ReactiveRedisTemplate<>(factory,
        RedisSerializationContext.<String, Long>newSerializationContext(jdkSerializationRedisSerializer)
            .key(UTF_8).value(longToStringSerializer).build());
  }
}
