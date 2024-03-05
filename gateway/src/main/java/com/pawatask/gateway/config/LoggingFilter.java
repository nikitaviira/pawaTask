package com.pawatask.gateway.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static com.pawatask.gateway.config.FilterOrder.LOGGING_FILTER;
import static com.pawatask.gateway.util.WebUtil.getIpAddress;
import static java.util.Objects.requireNonNull;
import static reactor.core.publisher.Mono.fromRunnable;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoggingFilter implements GlobalFilter, Ordered {
  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    ServerHttpRequest request = exchange.getRequest();
    HttpMethod method = request.getMethod();
    String path = request.getPath().value();
    return chain.filter(exchange)
        .doOnError((err) -> log.info("{} {} {} Error: {}", method, path, getIpAddress(exchange), err.getMessage()))
        .then(fromRunnable(() -> {
          int status = requireNonNull(exchange.getResponse().getStatusCode()).value();
          log.info("{} {} {} {}", method, path, getIpAddress(exchange), status);
        }));
  }

  @Override
  public int getOrder() {
    return LOGGING_FILTER.getOrder();
  }
}
