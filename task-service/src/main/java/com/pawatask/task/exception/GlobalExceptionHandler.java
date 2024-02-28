package com.pawatask.task.exception;

import com.pawatask.task.dto.ErrorDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;

import static java.util.Map.entry;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toMap;
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

  @ExceptionHandler(ServiceException.class)
  public ResponseEntity<ErrorDto> handleServiceException(ServiceException e) {
    return status(BAD_REQUEST).body(new ErrorDto(e.getMessage()));
  }

  @ExceptionHandler(UnauthorizedException.class)
  public ResponseEntity<ErrorDto> handleUnauthorizedException(UnauthorizedException e) {
    return status(UNAUTHORIZED).body(new ErrorDto(e.getMessage()));
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    Map<String, String> errors = ex
        .getBindingResult()
        .getFieldErrors()
        .stream()
        .map(f -> entry(f.getField(), requireNonNull(f.getDefaultMessage())))
        .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    return status(BAD_REQUEST).body(new ErrorDto("Validation failed", errors));
  }
}