package com.pawatask.gateway.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum FilterOrder {
  LOGGING_FILTER(-2),
  RATE_LIMIT_FILTER(-1),
  AUTH_FILTER(0);

  private final int order;
}
