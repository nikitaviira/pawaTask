package com.pawatask.kafka;

public enum KafkaTopics {
  USER(Names.USER);

  public final String label;

  public static class Names {
    public static final String USER = "user";
  }

  KafkaTopics(String label) {
    this.label = label;
  }
}