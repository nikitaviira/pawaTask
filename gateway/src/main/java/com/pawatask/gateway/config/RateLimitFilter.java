package com.pawatask.gateway.config;

import com.pawatask.gateway.exception.RateLimitExceeded;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import static java.time.Duration.ofMinutes;
import static java.time.Duration.ofSeconds;
import static java.util.Optional.ofNullable;
import static reactor.core.publisher.Mono.defer;
import static reactor.core.publisher.Mono.error;

@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimitFilter implements GlobalFilter, Ordered {
  private final ReactiveRedisTemplate<String, Long> redisTemplate;
  private final Long blockMinutesOnExceedingRPM = 5L;
  private final Long maxRequestsPerSecond = 100L;
  private final Long maxRequestsPerMinute = 1000L;

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    String ipAddress = getIpAddress(exchange);
    String blockedKey = "blocked:%s".formatted(ipAddress);
    return redisTemplate.opsForValue().get(blockedKey).hasElement()
        .flatMap(isBlocked -> isBlocked
                ? Mono.error(rateLimitExceeded(ipAddress))
                : Mono.zip(
                    perMinuteRateLimit(ipAddress, blockedKey),
                    perSecondRateLimit(ipAddress)
                  )
        )
        .then(chain.filter(exchange));
  }

  private Mono<Object> perMinuteRateLimit(String ipAddress, String blockedKey) {
    String perMinuteKey = "rpm:%s".formatted(ipAddress);

    return redisTemplate.opsForValue().get(perMinuteKey)
        .switchIfEmpty(defer(() -> redisTemplate.opsForValue().set(perMinuteKey, 0L, ofMinutes(1L)).thenReturn(0L)))
        .flatMap(value -> {
          if (value >= maxRequestsPerMinute) {
            return redisTemplate.opsForValue().set(blockedKey, -1L, ofMinutes(blockMinutesOnExceedingRPM))
                .flatMap(ignore -> error(rateLimitExceeded(ipAddress)));
          }
          return redisTemplate.opsForValue().increment(perMinuteKey);
        });
  }

  private Mono<Object> perSecondRateLimit(String ipAddress) {
    String perSecondKey = "rps:%s".formatted(ipAddress);
    return redisTemplate.opsForValue().get(perSecondKey)
        .switchIfEmpty(defer(() -> redisTemplate.opsForValue().set(perSecondKey, 0L, ofSeconds(1L)).thenReturn(0L)))
        .flatMap(value -> {
          if (value >= maxRequestsPerSecond) {
            return error(rateLimitExceeded(ipAddress));
          }
          return redisTemplate.opsForValue().increment(perSecondKey);
        });
  }

  private String getIpAddress(ServerWebExchange exchange) {
    return ofNullable(exchange.getRequest().getRemoteAddress())
        .map(InetSocketAddress::getAddress)
        .map(InetAddress::getHostAddress)
        .orElseThrow();
  }

  private RateLimitExceeded rateLimitExceeded(String ipAddress) {
    log.error("RATE LIMIT EXCEEDED: %s".formatted(ipAddress));
    return new RateLimitExceeded("Rate limit exceeded");
  }

  @Override
  public int getOrder() {
    return HIGHEST_PRECEDENCE;
  }
}