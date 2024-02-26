package com.pawatask.gateway.config;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RouteConfig {
  private final ServiceUrls serviceUrls;
  private final AuthFilter authFilter;

  @Bean
  public RouteLocator routeLocator(RouteLocatorBuilder builder) {
    return builder.routes()
        .route("auth-service", r -> r.path("/api/auth/**")
            .uri(serviceUrls.getAuthService()))
        .route("task-service", r -> r.path("/api/task/**")
            .filters(f -> f.filter(authFilter))
            .uri(serviceUrls.getTaskService()))
        .build();
  }
}
