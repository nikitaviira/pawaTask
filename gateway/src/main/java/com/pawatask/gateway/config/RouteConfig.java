package com.pawatask.gateway.config;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.springframework.http.HttpMethod.POST;

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
        .route("email-service", r -> r.method(POST).and().path("/sendgrid/processed-event")
            .uri(serviceUrls.getEmailService()))
        .route("task-service", r -> r.path("/api/task/**")
            .filters(f -> f.filter(authFilter))
            .uri(serviceUrls.getTaskService()))
        .build();
  }
}
