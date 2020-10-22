package dev.pgm.events.config;

import dev.pgm.events.Events;

public class EventsConfig {

  public static boolean observersMustReady() {
    return Events.get().getConfig().getBoolean("observers-must-ready");
  }
}
