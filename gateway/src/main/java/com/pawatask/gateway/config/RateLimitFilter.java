package com.pawatask.gateway.config;

import com.pawatask.gateway.exception.RateLimitExceeded;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static com.pawatask.gateway.config.FilterOrder.RATE_LIMIT_FILTER;
import static com.pawatask.gateway.util.WebUtil.getIpAddress;
import static java.time.Duration.ofMinutes;
import static java.time.Duration.ofSeconds;
import static reactor.core.publisher.Mono.defer;
import static reactor.core.publisher.Mono.error;

@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimitFilter implements GlobalFilter, Ordered {
  private final ReactiveRedisTemplate<String, Long> redisTemplate;
  @Value("${rateLimit.blockMinutes}")
  private Long blockMinutes;
  @Value("${rateLimit.maxRps}")
  private Long maxRequestsPerSecond;
  @Value("${rateLimit.maxRpm}")
  private Long maxRequestsPerMinute;

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
            return redisTemplate.opsForValue().set(blockedKey, -1L, ofMinutes(blockMinutes))
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

  private RateLimitExceeded rateLimitExceeded(String ipAddress) {
    log.error("RATE LIMIT EXCEEDED: %s".formatted(ipAddress));
    return new RateLimitExceeded("Rate limit exceeded");
  }

  @Override
  public int getOrder() {
    return RATE_LIMIT_FILTER.getOrder();
  }
}