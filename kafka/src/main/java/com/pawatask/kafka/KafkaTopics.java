package com.pawatask.kafka;

public enum KafkaTopics {
    USER_CREATED(Names.USER_CREATED);

    public static class Names {
        public static final String USER_CREATED = "userCreated";
    }

  KafkaTopics(String label) {
  }
}