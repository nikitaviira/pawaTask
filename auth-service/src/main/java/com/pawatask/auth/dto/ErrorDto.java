package com.pawatask.auth.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
public class ErrorDto {
  public String message;
  public List<String> fields = new ArrayList<>();

  public ErrorDto(String message) {
    this.message = message;
  }
}