package com.pawatask.gateway.exception;

import com.pawatask.gateway.dto.ErrorDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.ResponseEntity.status;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<ErrorDto> handleRuntimeException(RuntimeException e) {
    log.error("Unexpected error occurred while executing request: " + e.getMessage(), e);
    return status(INTERNAL_SERVER_ERROR).body(new ErrorDto("Something went wrong!"));
  }

  @ExceptionHandler(UnauthorizedException.class)
  public ResponseEntity<ErrorDto> handleUnauthorizedException(UnauthorizedException e) {
    return status(UNAUTHORIZED).body(new ErrorDto(e.getMessage()));
  }

  @ExceptionHandler(RateLimitExceeded.class)
  public ResponseEntity<ErrorDto> handleRateLimitExceeded(RateLimitExceeded e) {
    return status(TOO_MANY_REQUESTS).body(new ErrorDto(e.getMessage()));
  }
}