package com.pawatask.task.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {
  private final HeaderCallerContextArgumentResolver headerCallerContextArgumentResolver;

  public WebConfig(HeaderCallerContextArgumentResolver headerCallerContextArgumentResolver) {
    this.headerCallerContextArgumentResolver = headerCallerContextArgumentResolver;
  }

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
    argumentResolvers.add(headerCallerContextArgumentResolver);
  }
}