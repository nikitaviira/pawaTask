package com.pawatask.task.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort.Direction;

import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

@RequiredArgsConstructor
@Getter
public enum SortOrder {
  DEFAULT("createdAt", DESC),
  PRIORITY_ASC("priority", ASC),
  PRIORITY_DESC("priority", DESC),
  DUE_DATE_ASC("dueDate", ASC),
  DUE_DATE_DESC("dueDate", DESC);

  private final String dbField;
  private final Direction direction;
}
