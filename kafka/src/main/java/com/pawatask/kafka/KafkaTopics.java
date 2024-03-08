package com.pawatask.kafka;

public enum KafkaTopics {
  USER(Names.USER),
  EMAIL(Names.EMAIL);

  public final String label;

  public static class Names {
    public static final String USER = "user";
    public static final String EMAIL = "email";
  }

  KafkaTopics(String label) {
    this.label = label;
  }
}