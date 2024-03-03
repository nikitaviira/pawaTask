package com.pawatask.gateway.exception;

public class RateLimitExceeded extends RuntimeException {
  public RateLimitExceeded(String message) {
    super(message);
  }
}
