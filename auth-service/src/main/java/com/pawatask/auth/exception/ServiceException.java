package com.pawatask.auth.exception;

public class ServiceException extends RuntimeException {
  public ServiceException(String message) {
    super(message);
  }
}