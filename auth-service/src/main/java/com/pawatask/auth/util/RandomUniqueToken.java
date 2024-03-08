package com.pawatask.auth.util;

import org.springframework.stereotype.Component;

import static java.util.UUID.randomUUID;

@Component
public class RandomUniqueToken {
  public String generateRandomToken() {
    return randomUUID().toString().replace("-", "");
  }
}