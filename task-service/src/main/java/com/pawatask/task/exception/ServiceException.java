package com.pawatask.task.exception;

public class ServiceException extends RuntimeException {
  public ServiceException(String message) {
    super(message);
  }
}