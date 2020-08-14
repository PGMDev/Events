package dev.pgm.events.utils;

import java.time.Duration;

public class TimeFormatter {

  public static String seconds(Duration duration) {
    return duration.getSeconds() == 1
        ? duration.getSeconds() + " second"
        : duration.getSeconds() + " seconds";
  }
}
