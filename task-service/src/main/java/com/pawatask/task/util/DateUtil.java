package com.pawatask.task.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static java.time.ZoneId.of;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.Locale.ENGLISH;

public class DateUtil {
  public static final ZoneId TALLINN = of("Europe/Tallinn");
  public static final ZoneId UTC = of("UTC");

  public static final DateTimeFormatter DATE_FORMATTER = ofPattern("dd.MM.yyyy")
      .withZone(TALLINN);
  public static final DateTimeFormatter DATE_TIME_FORMATTER = ofPattern("dd.MM.yyyy HH:mm")
      .withZone(TALLINN);
  public static final DateTimeFormatter FULL_DATE_TIME_FORMATTER = ofPattern("d MMM. HH:mm", ENGLISH)
      .withZone(TALLINN);

  public static String formatDate(LocalDate date) {
    return date.format(DATE_FORMATTER);
  }

  public static String formatDateTime(LocalDateTime dateTime) {
    return convertToLocalTime(dateTime).format(DATE_TIME_FORMATTER);
  }

  public static String formatFullDateTime(LocalDateTime dateTime) {
    return convertToLocalTime(dateTime).format(FULL_DATE_TIME_FORMATTER);
  }

  private static ZonedDateTime convertToLocalTime(LocalDateTime dateTime) {
    return dateTime.atZone(UTC).withZoneSameInstant(TALLINN);
  }
}