package com.pawatask.task.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
public class ErrorDto {
  public String message;
  public Map<String, String> fields = new HashMap<>();

  public ErrorDto(String message) {
    this.message = message;
  }
}